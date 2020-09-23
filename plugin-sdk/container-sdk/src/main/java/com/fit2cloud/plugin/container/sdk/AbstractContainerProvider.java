package com.fit2cloud.plugin.container.sdk;

import com.fit2cloud.plugin.container.sdk.constants.ResourceType;
import com.fit2cloud.plugin.container.sdk.model.*;
import com.fit2cloud.plugin.container.sdk.request.*;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.jar.JarFile;

public abstract class AbstractContainerProvider implements IContainerProvider {

    public String getCredentialPageTemplate() {
        return readFileFromJar("credential.json");
    }

    public String getPageTemplate(ResourceType resourceType) {
        switch (resourceType) {
            case QUOTA:
                return readFileFromJar("quota.json");
            default:
                return null;
        }
    }

    private String readFileFromJar(String fileName) {
        InputStream is = null;
        try {
            URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
            JarFile jarFile = new JarFile(url.getPath());
            is = jarFile.getInputStream(jarFile.getEntry(fileName));
            return IOUtils.toString(is);
        } catch (Exception e) {
            throw new RuntimeException("failed to read " + fileName, e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public abstract boolean validateCredential(String credential);

    public abstract List<Node> getNodes(BaseRequest request);

    public abstract List<Project> getProjects(BaseRequest request);

    public abstract List<Application> getApplications(GetApplicationRequest request);

    public abstract List<Pod> getPods(GetPodRequest request);

    public abstract List<Service> getServices(GetServiceRequest request);

    public abstract List<Route> getRoutes(GetRouteRequest request);

    public abstract List<PersistentVolume> getPersistentVolumes(BaseRequest request);

    public abstract List<PersistentVolumeClaim> getPersistentVolumeClaims(GetPersistentVolumeClaimRequest request);

    public abstract List<Quota> getQuotas(GetQuotaAndLimitRequest request);

    public abstract List<Limit> getLimits(GetQuotaAndLimitRequest request);

    public abstract List<Role> getRoles(BaseRequest request);

    public abstract List<RoleBinding> getClusterRoleBindings(BaseRequest request);

    public abstract List<RoleBinding> getRoleBindings(GetRoleBindingRequest request);

    public abstract List<User> getUsers(BaseRequest request);

    public abstract List<Group> getGroups(BaseRequest request);

    public abstract Project createProject(CreateProjectRequest request);

    public abstract boolean deleteProject(DeleteProjectRequest request);

    public abstract void createQuota(CreateQuotaRequest request);

    public abstract boolean deleteQuota(DeleteQuotaRequest request);

    public abstract void bindRole(BindRoleRequest request);


}
