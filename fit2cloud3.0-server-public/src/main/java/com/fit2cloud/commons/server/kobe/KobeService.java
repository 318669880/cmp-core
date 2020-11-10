package com.fit2cloud.commons.server.kobe;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

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
        Kobe.ProxyConfig.Builder proxyBuilder = Kobe.ProxyConfig.newBuilder().setEnable(false);
        if (request.getProxy() != null) {
//            builder.putVars("ansible_ssh_common_args", request.getProxy());
            String proxyJson = request.getProxy();
            JSONObject jsonObject = JSONObject.parseObject(proxyJson);
            proxyBuilder.setEnable(true);
            proxyBuilder.setIp(jsonObject.getString("ip"));
            proxyBuilder.setPort(jsonObject.getIntValue("port"));
            proxyBuilder.setUser(jsonObject.getString("username"));
            proxyBuilder.setPassword(jsonObject.getString("password"));
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
        if (StringUtils.isNotEmpty(executePath)) {
            args = "executable=" + executePath + " " + content;
        } else {
            args = content;
        }
        if (request.getVariables() != null) {
            Iterator<Map.Entry<String, String>> iterator = request.getVariables().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                args = args.replaceAll("\\{\\{" + next.getKey() + "}}", next.getValue());
            }
        }

        Kobe.RunAdhocRequest adhocRequest = Kobe.RunAdhocRequest.newBuilder()
                .setInventory(
                        Kobe.Inventory.newBuilder().addHosts(
                                builder
                                        .setIp(request.getIp())
                                        .setName("default-host")
                                        .setUser(request.getUsername())
                                        .setPort(request.getPort())
                                        .setProxyConfig(proxyBuilder.build())
                                        .build()
                        )
                                .addGroups(
                                        Kobe.Group.newBuilder()
                                                .setName("default-group")
                                                .addHosts("default-host")
                                                .putAllVars(builder.getVarsMap())
                                                .build()
                                )
                                .putAllVars(builder.getVarsMap())
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

    public String watchTaskResult(String taskId) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(kobeHost, kobePort).usePlaintext().build();
        KobeApiGrpc.KobeApiBlockingStub blockingStub = KobeApiGrpc.newBlockingStub(channel);
        Kobe.WatchRequest watchRequest = Kobe.WatchRequest.newBuilder().setTaskId(taskId).build();
        Iterator<Kobe.WatchStream> watchStreamIterator = blockingStub.watchResult(watchRequest);
        StringBuilder stringBuilder = new StringBuilder();
        while (watchStreamIterator.hasNext()) {
            Kobe.WatchStream next = watchStreamIterator.next();
            ByteString stream = next.getStream();
            stringBuilder.append(stream.toStringUtf8());
        }
        return stringBuilder.toString();
    }

    public Kobe.GetResultResponse runAdhocGetResult(AdhocRequest request) throws Exception {
        Kobe.RunAdhocResult runAdhocResult = runAdhoc(request);
        return getTaskResult(runAdhocResult.getResult().getId(), request.getTimeout());
    }
}
