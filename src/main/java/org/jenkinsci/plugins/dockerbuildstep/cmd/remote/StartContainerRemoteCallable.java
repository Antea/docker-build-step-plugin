package org.jenkinsci.plugins.dockerbuildstep.cmd.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import hudson.model.Descriptor;
import hudson.remoting.Callable;
import org.jenkinsci.plugins.dockerbuildstep.DockerBuilder.Config;
import org.jenkinsci.plugins.dockerbuildstep.cmd.DockerCommand;
import org.jenkinsci.remoting.RoleChecker;

import java.io.Serializable;

/**
 * A Callable wrapping the start container command.
 * It can be sent through a Channel to execute on the correct build node.
 * 
 * @author David Csakvari
 */
public class StartContainerRemoteCallable implements Callable<String, Exception>, Serializable {

    private static final long serialVersionUID = 8479489609579635741L;

    Config cfgData;
    Descriptor<?> descriptor;
    
    String id;
    
    public StartContainerRemoteCallable(Config cfgData, Descriptor<?> descriptor, String id) {
        this.cfgData = cfgData;
        this.descriptor = descriptor;
        this.id = id;
    }
    
    public String call() throws Exception {
        DockerClient client = DockerCommand.getClient(descriptor, cfgData.dockerUrlRes, cfgData.dockerVersionRes, cfgData.dockerCertPathRes, null);

        client.startContainerCmd(id).exec();
        InspectContainerResponse inspectResp = client.inspectContainerCmd(id).exec();

        ObjectMapper mapper = new ObjectMapper();
        String serialized = mapper.writeValueAsString(inspectResp);
        return serialized;
    }

    @Override
    public void checkRoles(RoleChecker checker) throws SecurityException {

    }
}
