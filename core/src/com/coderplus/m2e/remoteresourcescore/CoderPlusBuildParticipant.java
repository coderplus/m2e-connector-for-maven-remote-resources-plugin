package com.coderplus.m2e.remoteresourcescore;
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
import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class CoderPlusBuildParticipant extends MojoExecutionBuildParticipant {



	private static final String INCLUDES = "includes";
	private static final String BUNDLE_GOAL = "bundle";
	private static final String RESOURCES_DIRECTORY = "resourcesDirectory";
	private static final String PROCESS_GOAL = "process";
	private static final String OUTPUT_DIRECTORY = "outputDirectory";
	private static final String[] DEFAULT_INCLUDES = new String[]{ "**/*.txt", "**/*.vm", };
	public CoderPlusBuildParticipant(MojoExecution execution) {

		super(execution, true);
	}

	@Override
	public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception {


		final MojoExecution execution = getMojoExecution();

		if (execution == null) {
			return null;
		}
		IMaven maven = MavenPlugin.getMaven();	
		MavenProject project = getMavenProjectFacade().getMavenProject();
		final BuildContext buildContext = getBuildContext();
		final IFile pomFile = (IFile) getMavenProjectFacade().getProject().findMember("pom.xml");
		
		if(buildContext.isIncremental()){
			if(!buildContext.hasDelta(pomFile.getLocation().toFile()) && PROCESS_GOAL.equals(execution.getGoal())) {
				//ignore if there were no changes to the pom and was an incremental build
				return null;
			} else if(BUNDLE_GOAL.equals(execution.getGoal())){
				File resourcesDirectory = maven.getMojoParameterValue(project, execution, RESOURCES_DIRECTORY, File.class, new NullProgressMonitor());
				Scanner ds = buildContext.newScanner(resourcesDirectory); 
				if (ds != null) {
					ds.setIncludes( DEFAULT_INCLUDES );
					String[] includes = maven.getMojoParameterValue(project, execution, INCLUDES,String[].class, new NullProgressMonitor());
					if(includes!= null && includes.length!=0){
						ds.setIncludes(includes);
					}
					ds.scan();
					String[] includedResourceFiles = ds.getIncludedFiles();
					if(includedResourceFiles == null || includedResourceFiles.length == 0 ){
						//ignore if there were no changes to the resources and was an incremental build
						return null;
					}
				}
			}
		}

		File outputDirectory = maven.getMojoParameterValue(project, execution, OUTPUT_DIRECTORY,File.class, new NullProgressMonitor());

		setTaskName(monitor);
		//execute the maven mojo
		final Set<IProject> result = executeMojo(kind, monitor);

		if(outputDirectory.exists()){
			//FIXME: Should we read the bundle manifests and refresh only the required files
			//this refresh can take a long time!
			buildContext.refresh(outputDirectory);
		}

		return result;
	}

	private void setTaskName(IProgressMonitor monitor) throws CoreException {

		if (monitor != null) {
			final String taskName = String.format("CoderPlus M2E: Invoking %s on %s", getMojoExecution().getMojoDescriptor()
					.getFullGoalName(), getMavenProjectFacade().getProject().getName());
			monitor.setTaskName(taskName);
		}
	}

	private Set<IProject> executeMojo(final int kind, final IProgressMonitor monitor) throws Exception {

		return super.build(kind, monitor);
	}


}
