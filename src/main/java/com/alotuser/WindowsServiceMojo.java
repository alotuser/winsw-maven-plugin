package com.alotuser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.alotuser.util.ResUtil;
import com.alotuser.util.ResourcesUtil;
import com.alotuser.util.WinVersionUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;

@Mojo(name = "winsw", defaultPhase = LifecyclePhase.PACKAGE)
public class WindowsServiceMojo extends AbstractMojo {

	@Parameter(property = "project.version", required = true)
	private String version;

	@Parameter(property = "project.groupId", required = true, defaultValue = "")
	private String groupId;

	@Parameter(property = "project.artifactId", required = true, defaultValue = "")
	private String artifactId;

	@Parameter(property = "project.description")
	private String description;

	@Parameter(property = "project.packaging")
	private String packaging;

	@Parameter(property = "project.basedir", required = true, readonly = true)
	private File baseDir;

	@Parameter(property = "project.build.directory", required = true)
	private File targetDir;

	@Parameter(property = "project.build.sourceDirectory", required = true, readonly = true)
	private File sourceDir;

	@Parameter(property = "project.build.testSourceDirectory", required = true, readonly = true)
	private File testSourceDir;

	@Parameter(property = "arguments")
	private String[] arguments;

	@Parameter(property = "vmOptions")
	private String vmOptions;

	@Parameter(property = "programExecutable", defaultValue = "java")
	private String programExecutable;

	@Parameter(property = "programArguments")
	private String programArguments;

	@Parameter(property = "projectName")
	private String projectName;

	@Parameter(property = "isVersion", defaultValue = "true")
	private boolean isVersion;// ?????????????????????

	@Parameter(property = "winVersion", defaultValue = "x64")
	private String winVersion;// win????????? x64 x86 net2 net4 net461

	public void execute() {
		getLog().info("???????????? Windows Service ???????????????");

		String wv = WinVersionUtil.getEqualsStrIgnoreCase(winVersion, "x64", "x86", "net2", "net4", "net461");
		if (null == wv) {
			getLog().info("?????????winVersion" + winVersion + "?????????:x64 x86 net2 net4 net461");
			return;
		}
		wv = wv.toUpperCase();
		getLog().info("winVersion:" + wv);
		try {
			String jarName = getJarName(isVersion), jarNames = getJarName(isVersion);
			if (!FileUtil.exist(targetDir.getPath() + File.separator + jarNames)) {
				jarNames = getJarName();
			}
			if (!FileUtil.exist(targetDir.getPath() + File.separator + jarNames)) {
				jarNames = getJarName(!isVersion);
			}
			if (FileUtil.exist(targetDir.getPath() + File.separator + jarNames)) {
				/* ??????????????? */
				File distDir = new File(targetDir, File.separator + "dist");
				if (distDir.exists()) {
					try {
						FileUtil.del(distDir);
					} catch (Exception e) {
						getLog().error("???????????????????????????????????????????????????");
						e.printStackTrace();
					}
				}
				FileUtil.mkdir(distDir.getPath());
				File logDir = new File(distDir, File.separator + "logs");
				FileUtil.mkdir(logDir.getPath());
				String jarPrefixName = getJarPrefixName(isVersion);
				/* ???????????? */
				String resName = StrUtil.concat(true, "WinSW-", wv, ".exe.yml");

				ResUtil.writeWinFile(resName, new File(distDir, File.separator + jarPrefixName + ".exe"));
				FileUtil.writeString(format(jarName, jarPrefixName), new File(distDir, File.separator + jarPrefixName + ".xml"), CharsetUtil.UTF_8);
				FileUtil.writeString(ResourcesUtil.README_FILE, new File(distDir, File.separator + "readme.txt"), CharsetUtil.UTF_8);
				FileUtil.writeString(ResourcesUtil.CONFIG_FILE, new File(distDir, File.separator + jarPrefixName + ".exe.config"), CharsetUtil.UTF_8);
				FileUtil.copy(new File(targetDir.getPath() + File.separator + jarNames), new File(distDir, File.separator + jarName), true);

				 

				createBat(distDir, "install.bat", "install");
				createBat(distDir, "uninstall.bat", "uninstall");
				createBat(distDir, "start.bat", "start");
				createBat(distDir, "stop.bat", "stop");
				createBat(distDir, "restart.bat", "restart");

				getLog().info("?????????????????????....");
				String zipDir = targetDir.getPath() + File.separator + jarPrefixName + ".zip";
				ZipUtil.zip(distDir.getPath(), zipDir);
				getLog().info("????????????????????????....");
				FileUtil.del(distDir);
				getLog().info("?????????????????????:" + zipDir);
			} else {
				getLog().info("??????Windows Service ??????:???????????????");
			}

		} catch (Exception e) {
			getLog().error("??????Windows Service ?????????", e);
		}
	}

	/**
	 * ????????????
	 * 
	 * @param jarName       jarName
	 * @param jarPrefixName jarPrefixName
	 * @return ???????????????
	 */
	private String format(String jarName, String jarPrefixName) {

		if (arguments.length > 0) {
			getLog().warn("arguments ?????????????????????,??????????????????????????????,??????????????? vmOptions ?????? ??? programArguments ??????");
		}
		String vm_options = StrUtil.isEmpty(vmOptions) ? " " : " " + vmOptions + " ";
		String program_arguments = StrUtil.isEmpty(programArguments) ? "" : " " + programArguments;

		Map<String, Object> elements = new LinkedHashMap<String, Object>();

		elements.put("id", artifactId);
		elements.put("name", jarPrefixName);
		elements.put("description", null == description ? "????????????" : description);
		elements.put("envName", jarPrefixName.toUpperCase());
		elements.put("executable", programExecutable);
		elements.put("arguments", vm_options + "-jar " + jarName + program_arguments);

		return StrUtil.format(ResourcesUtil.XML_FILE, elements);

	}

	/**
	 * @param outDri   ????????????
	 * @param fileName ?????????
	 * @param text     ????????????
	 */
	private void createBat(File outDri, String fileName, String text) {
		if (!outDri.exists()) {
			FileUtil.mkdir(outDri.getPath());
		}
		File file = new File(outDri, fileName);
		try (FileWriter w = new FileWriter(file)) {
			w.write("@echo off\n" + "%1 mshta vbscript:CreateObject(\"Shell.Application\").ShellExecute(\"cmd.exe\",\"/c %~s0 ::\",\"\",\"runas\",1)(window.close)&&exit\n" + "%~dp0" + getJarPrefixName(isVersion) + ".exe " + text + "\n" + "echo The " + getJarPrefixName(isVersion) + " service current state:\n" + "%~dp0" + getJarPrefixName(isVersion) + ".exe status\n" + "pause");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ??????jar????????????
	 * 
	 * @return String
	 */
	private String getJarPrefixName() {
		return getJarPrefixName(true);
	}

	/**
	 * ??????jar????????????
	 * 
	 * @param isVersion
	 * @return String
	 */
	private String getJarPrefixName(boolean isVersion) {
		return artifactId + (isVersion ? "-" + version : "");
	}

	/**
	 * ??????jar?????????
	 * 
	 * @return String
	 */
	private String getJarName() {
		return getJarPrefixName() + "." + packaging;
	}

	/**
	 * ??????jar?????????
	 * 
	 * @param isVersion
	 * @return String
	 */
	private String getJarName(boolean isVersion) {
		return getJarPrefixName(isVersion) + "." + packaging;
	}

}
