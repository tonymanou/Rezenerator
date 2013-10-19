package com.tlorrain.android.rezenerator;

import static org.apache.maven.plugins.annotations.LifecyclePhase.INITIALIZE;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.tlorrain.android.rezenerator.core.Configuration;
import com.tlorrain.android.rezenerator.core.RezeneratorRunner;
import com.tlorrain.android.rezenerator.core.log.Logger;

/**
 * Goal generating png resources from source (svg...) files
 * 
 */
@Mojo(name = "generate", defaultPhase = INITIALIZE)
public class GenerateMojo extends AbstractMojo {

	/**
	 * Directory containing the source files.
	 * 
	 */
	@Parameter(required = true, defaultValue = "${project.basedir}/drawable")
	private File inputDirectory;

	/**
	 * Directory where the images will be generated.
	 * 
	 */
	@Parameter(required = true, defaultValue = "${project.basedir}/res")
	private File outputDirectory;

	/**
	 * Directory where the images will be cached to speed up the build.
	 * 
	 */
	@Parameter(defaultValue = "${project.basedir}/.rezenerator-cache~")
	private File cacheDirectory;

	/**
	 * Packages to scan for extra processors.
	 */
	@Parameter
	private List<String> scannedPackages;

	@Parameter
	private List<File> definitionDirs;

	public void execute() throws MojoExecutionException {
		getLog().info("input dir : " + inputDirectory);
		getLog().info("output dir : " + outputDirectory);
		getLog().info("cache dir : " + cacheDirectory);

		Configuration configuration = new Configuration();

		configuration.setInDir(inputDirectory)//
				.setBaseOutDir(outputDirectory)//
				.addScannedPackage("com.tlorrain.android")// TODO add this by
															// default in conf
				.setLogger(new MavenLogger());

		if (scannedPackages != null) {
			for (String pkg : scannedPackages) {
				getLog().info("add scanned package: " + pkg);
				configuration.addScannedPackage(pkg);
			}
		}

		if (definitionDirs != null) {
			for (File dir : definitionDirs) {
				configuration.addDefinitionDir(dir);
			}
		} else {
			configuration.addDefinitionDir(new File("definitions"));
		}

		if (System.getProperties().getProperty("rezenerator.force.update") != null) {
			configuration.setForceUpdate(true);
		}

		new RezeneratorRunner().run(configuration).isSuccessful();

	}

	private class MavenLogger implements Logger {

		@Override
		public void info(String info) {
			GenerateMojo.this.getLog().info(info);
		}

		@Override
		public void verbose(String debug) {
			GenerateMojo.this.getLog().debug(debug);
		}

		@Override
		public void verbose(Exception exception) {
			GenerateMojo.this.getLog().debug(exception);
		}

		@Override
		public void error(String error) {
			GenerateMojo.this.getLog().error(error);
		}

	}
}
