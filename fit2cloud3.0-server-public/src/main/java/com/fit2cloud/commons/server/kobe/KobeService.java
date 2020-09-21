package com.fit2cloud.commons.server.kobe;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author gin
 * @Date 2020/8/25 4:31 下午
 */
@Service
public class KobeService {
    @Value("${kobe.host}")
    private String kobeHost;
    @Value("${kobe.port}")
    private Integer kobePort;

    public Kobe.RunAdhocResult runAdhoc(AdhocRequest request) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(kobeHost, kobePort).usePlaintext().build();
        KobeApiGrpc.KobeApiBlockingStub blockingStub = KobeApiGrpc.newBlockingStub(channel);

        String content = request.getContent();
        String header = request.getHeader();
        String executePath = request.getExecutePath();

        Kobe.Host.Builder builder = Kobe.Host.newBuilder();
        if (request.getVars().size() > 0) {
            builder.putAllVars(request.getVars());
        }
        if (request.getProxy() != null) {
            builder.putVars("ansible_ssh_common_args", request.getProxy());
        }
        if (request.getCloudServerCredentailType().equals(CloudServerCredentialType.KEY)) {
            builder.setPrivateKey(request.getCredential());
        } else {
            builder.setPassword(request.getCredential());
        }
        if (request.getBecome()) {
            builder.putVars("become", "yes");
            builder.putVars("become_method", request.getBecomeMethod());
        }
        if (header != null) {
            content = header + content;
        }
        String args;
        if (executePath != null) {
            args = "executable=" + executePath + " " + content;
        } else {
            args = content;
        }

        Kobe.RunAdhocRequest adhocRequest = Kobe.RunAdhocRequest.newBuilder()
                .setInventory(
                        Kobe.Inventory.newBuilder().addHosts(
                                builder
                                        .setIp(request.getIp())
                                        .setName("default-host")
                                        .setUser(request.getUsername())
                                        .setPort(request.getPort())
                                        .build()
                        )
                                .addGroups(
                                        Kobe.Group.newBuilder()
                                                .setName("default-group")
                                                .addHosts("default-host")
                                                .build()
                                )
                                .build()
                )
                .setModule(request.getModule())
                .setParam(args)
                .setPattern("default-host")
                .build();// params

        Kobe.RunAdhocResult runAdhocResult = blockingStub.runAdhoc(adhocRequest);
        channel.shutdown();
        return runAdhocResult;
    }

    public Kobe.RunPlaybookResult runPlaybook(String project, String playbook, Kobe.Inventory inventory) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(kobeHost, kobePort).usePlaintext().build();
        KobeApiGrpc.KobeApiBlockingStub blockingStub = KobeApiGrpc.newBlockingStub(channel);

        Kobe.RunPlaybookRequest playbookRequest = Kobe.RunPlaybookRequest.newBuilder()
                .setInventory(inventory)
                .setProject(project)
                .setPlaybook(playbook)
                .build();// params

        Kobe.RunPlaybookResult runPlaybookResult = blockingStub.runPlaybook(playbookRequest);
        channel.shutdown();
        return runPlaybookResult;
    }

    public Kobe.GetResultResponse getTaskResult(String taskId, Long timeout) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(kobeHost, kobePort).usePlaintext().build();
        KobeApiGrpc.KobeApiBlockingStub blockingStub = KobeApiGrpc.newBlockingStub(channel);

        long times = timeout <= 3600L && timeout >= 0L ? timeout : 3600L;
        Kobe.GetResultResponse taskResult = null;

        while (--times > 0L) {
            Kobe.GetResultRequest getResultRequest = Kobe.GetResultRequest.newBuilder().setTaskId(taskId).build();
            taskResult = blockingStub.getResult(getResultRequest);
            if (taskResult.getItem().getFinished()) {
                break;
            }
            Thread.sleep(1000L);
        }
        channel.shutdown();
        if (times <= 0L) {
            throw new Exception(taskId + "task time out");
        } else {
            return taskResult;
        }
    }

    public Kobe.GetResultResponse runAdhocGetResult(AdhocRequest request) throws Exception {
        Kobe.RunAdhocResult runAdhocResult = runAdhoc(request);
        return getTaskResult(runAdhocResult.getResult().getId(), request.getTimeout());
    }
}
