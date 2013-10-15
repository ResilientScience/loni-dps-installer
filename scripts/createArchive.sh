mkdir -p pipelineServerInstaller/dist/lib
mkdir -p pipelineServerInstaller/dist/install_files

cp dist/PipelineServerInstaller.jar ./pipelineServerInstaller/dist
cp launchInstaller.sh ./pipelineServerInstaller/
cp makefile ./pipelineServerInstaller/dist/install_files/
cp postInstall.sh ./pipelineServerInstaller/dist/install_files/
cp install*.sh ./pipelineServerInstaller/dist/install_files/
cp checkHost.sh ./pipelineServerInstaller/dist/install_files/
cp checkSGE.sh ./pipelineServerInstaller/dist/install_files/
cp -R lib/*.jar ./pipelineServerInstaller/dist/lib/
cp ReleaseNotes ./pipelineServerInstaller/
cp DefaultInstallationPreferencesFile.xml ./pipelineServerInstaller/dist/install_files/

tar -czvf pipelineServerInstaller.tar.gz ./pipelineServerInstaller
rm -Rf pipelineServerInstaller

 

