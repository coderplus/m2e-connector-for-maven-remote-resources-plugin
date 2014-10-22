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
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IClasspathEntryDescriptor;

@SuppressWarnings({ "deprecation" })
public class CoderPlusProjectConfigurator extends AbstractJavaProjectConfigurator {

	private static final String OUTPUT_DIRECTORY = "outputDirectory";
	private static final String ATTACHED_TO_TEST = "attachedToTest";
	private static final String ATTACHED_TO_MAIN = "attachedToMain";
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
			boolean attachedToMain = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, ATTACHED_TO_MAIN,Boolean.class, new NullProgressMonitor()));
			boolean attachedToTest = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, ATTACHED_TO_TEST,Boolean.class, new NullProgressMonitor()));
			File outputDirectory = maven.getMojoParameterValue(mavenProject, execution, OUTPUT_DIRECTORY,File.class, new NullProgressMonitor());
			IClasspathEntryDescriptor descriptor = null;
			if(attached && attachedToMain){
				IPath relativeSourcePath = MavenProjectUtils.getProjectRelativePath(project,outputDirectory.getAbsolutePath());
				descriptor  = classpath.addSourceEntry(project.getFullPath().append(relativeSourcePath),facade.getOutputLocation(), true);
			} else  if(attached && attachedToTest){
				IPath relativeSourcePath = MavenProjectUtils.getProjectRelativePath(project,outputDirectory.getAbsolutePath());
				descriptor = classpath.addSourceEntry(project.getFullPath().append(relativeSourcePath),facade.getTestOutputLocation(), true);
			}

			if(descriptor!= null){
				IPath[] paramArrayOfIPath = new IPath[1];
				paramArrayOfIPath[0] = new Path("**");
				descriptor.setExclusionPatterns(paramArrayOfIPath);
			}

		}
	}


}
