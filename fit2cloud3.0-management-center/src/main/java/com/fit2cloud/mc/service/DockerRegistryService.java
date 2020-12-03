package com.fit2cloud.mc.service;


import com.fit2cloud.mc.dto.DockerRegistry;
import com.fit2cloud.mc.model.ModelManager;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class DockerRegistryService {

    @Resource
    private ModelManagerService modelManagerService;

    public DockerRegistry get(){
        AtomicReference<DockerRegistry> atomicReference = new AtomicReference<>();
        ModelManager managerInfo = modelManagerService.select();
        Optional.ofNullable(managerInfo.getDockerRegistry()).ifPresent(json -> {
            DockerRegistry registry = new Gson().fromJson(json, DockerRegistry.class);
            atomicReference.set(registry);
        });
        return atomicReference.get();
    }
}
