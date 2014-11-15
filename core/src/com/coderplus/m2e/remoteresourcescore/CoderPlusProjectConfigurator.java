/*******************************************************************************
 * Copyright (c) 2014 Aneesh Joseph
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Aneesh Joseph(coderplus.com)
 *******************************************************************************/
package com.coderplus.m2e.remoteresourcescore;

import java.io.File;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectUtils;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

public class CoderPlusProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {

	private static final String OUTPUT_DIRECTORY = "outputDirectory";
	private static final String ATTACH_TO_TEST = "attachToTest";
	private static final String ATTACH_TO_MAIN = "attachToMain";
	private static final String ATTACHED = "attached";

	@Override
	public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,IPluginExecutionMetadata executionMetadata) {
		return new CoderPlusBuildParticipant(execution);
	}

	@Override
	public void configureRawClasspath(ProjectConfigurationRequest request,IClasspathDescriptor classpath, IProgressMonitor monitor)
			throws CoreException {
		IMavenProjectFacade facade = request.getMavenProjectFacade();
		MavenProject mavenProject = facade.getMavenProject();
		IProject project = facade.getProject();
		assertHasNature(request.getProject(), "org.eclipse.jdt.core.javanature");
		for (MojoExecution execution : getMojoExecutions(request, monitor)) {
			boolean attached = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, ATTACHED,Boolean.class, new NullProgressMonitor()));
			boolean attachToMain = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, ATTACH_TO_MAIN,Boolean.class, new NullProgressMonitor()));
			boolean attachToTest = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, ATTACH_TO_TEST,Boolean.class, new NullProgressMonitor()));
			File outputDirectory = maven.getMojoParameterValue(mavenProject, execution, OUTPUT_DIRECTORY,File.class, new NullProgressMonitor());
			IClasspathEntryDescriptor descriptor = null;
			String version = execution.getVersion();
			if(version.startsWith("1.0") || version.startsWith("1.1") || version.startsWith("1.2") || version.startsWith("1.3") || version.startsWith("1.4")){
				attachToMain=true;
				attachToTest=true;
			}
			if(attached && attachToMain){
				IPath relativeSourcePath = MavenProjectUtils.getProjectRelativePath(project,outputDirectory.getAbsolutePath());
				descriptor  = classpath.addSourceEntry(project.getFullPath().append(relativeSourcePath),facade.getOutputLocation(), true);
				Resource resource = new Resource();
				resource.setDirectory(outputDirectory.getAbsolutePath());
				mavenProject.addResource(resource);
			} else  if(attached && attachToTest){
				IPath relativeSourcePath = MavenProjectUtils.getProjectRelativePath(project,outputDirectory.getAbsolutePath());
				descriptor = classpath.addSourceEntry(project.getFullPath().append(relativeSourcePath),facade.getTestOutputLocation(), true);
				Resource resource = new Resource();
				resource.setDirectory(outputDirectory.getAbsolutePath());
				mavenProject.addTestResource(resource);
			}

			if(descriptor!= null){
				IPath[] paramArrayOfIPath = new IPath[1];
				paramArrayOfIPath[0] = new Path("**");
				descriptor.setExclusionPatterns(paramArrayOfIPath);
			}

		}
	}


}
