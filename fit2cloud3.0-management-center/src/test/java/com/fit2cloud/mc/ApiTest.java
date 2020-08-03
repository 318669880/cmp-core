package com.fit2cloud.mc;

import com.alibaba.fastjson.JSON;
import org.apache.commons.net.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * demo示例
 */
public class ApiTest {

    private static String accessKey = "x8eMvuKxpaBSNRJ7";
    private static String secretKey = "adsyA2cGQtAmLGZE";
    private static String sourceId = "admin";

    private final static String CMP_MANAGEMENT_CENTER_URL = "https://rddev2.fit2cloud.com/management-center/";
    private static String CREATE_USER_URL = CMP_MANAGEMENT_CENTER_URL + "user/add";
    private static String CREATE_USER_ORGANIZATION_URL = CMP_MANAGEMENT_CENTER_URL + "user/add/organization";
    private static String CREATE_USER_WORKSPACE_URL = CMP_MANAGEMENT_CENTER_URL + "user/add/workspace";
    private static String CREATE_WORKSPACE_URL = CMP_MANAGEMENT_CENTER_URL + "workspace/add";
    private static String CREATE_ORGANIZATION_URL = CMP_MANAGEMENT_CENTER_URL + "organization/add";
    private static String ADD_ROLE_TO_USER_URL = CMP_MANAGEMENT_CENTER_URL + "user/add/role";
    private static String DELETE_ROLE_FROM_USER_URL = CMP_MANAGEMENT_CENTER_URL + "user/delete/role";
    private static String GET_ORGANIZATION_USER_LIST_URL = CMP_MANAGEMENT_CENTER_URL + "organization/user/{organizationId}/{goPage}/{pageSize}";
    private static String GET_WORKSPACE_USER_LIST_URL = CMP_MANAGEMENT_CENTER_URL + "workspace/user/{workspaceId}/{goPage}/{pageSize}";

    public static void main(String[] args) throws Exception {
        System.out.println(getSignature());
        //1.创建用户(没有角色)
        //createUser();
        //2.创建组织管理员
        //createUserOrganization();
        //3.创建工作空间用户
        //createUserWorkspace();
        //4.创建工作空间
        //createWorkspace();
        //5.创建组织
        //createOrganization();
        //6.给定用户赋角色
//        addRoleToUser();
//        //获取组织管理员
//        getOrganizationUserList();
//        //获取工作空间用户
//        getworkspaceUserList();

    }

    private static void deleteRoleFromUser() {
        Map<String, Object> param = new HashMap<>();
        //用户ID 唯一(登录标识) 必填
        param.put("roleId", "");
        param.put("userId", "");

        try {
            String result = post(DELETE_ROLE_FROM_USER_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createUser() {
        Map<String, Object> param = new HashMap<>();
        //用户ID 唯一(登录标识) 必填
        param.put("id", "");
        param.put("name", "");

        param.put("email", "");
        param.put("password", "");
        param.put("phone", "");
        try {
            String result = post(CREATE_USER_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createUserOrganization() {
        Map<String, Object> param = new HashMap<>();
        //用户ID 唯一(登录标识) 必填
        param.put("id", "DB_API_TEST");
        param.put("name", "DB_API_TEST");

        param.put("email", "DB_API_TEST@fit2cloud.com");
        param.put("password", "DB_API_TEST@fit2cloud.com");
        param.put("phone", "17610170839");

        //角色为组织管理员或继承组织管理员
        param.put("roleId", "asasasasasasas");

        List<String> organizationIds = new ArrayList<>();
        organizationIds.add("asasasas");
        param.put("organizationIds", organizationIds);
        try {
            String result = post(CREATE_USER_ORGANIZATION_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createUserWorkspace() {
        Map<String, Object> param = new HashMap<>();
        //用户ID 唯一(登录标识) 必填
        param.put("id", "");
        param.put("name", "");

        param.put("email", "");
        param.put("password", "");
        param.put("phone", "");

        //角色为工作空间用户或继承工作空间用户
        param.put("roleId", "");
        List<String> workspaceIds = new ArrayList<>();
        workspaceIds.add("");
        param.put("workspaceIds", workspaceIds);

        try {
            String result = post(CREATE_USER_WORKSPACE_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createOrganization() {
        Map<String, Object> param = new HashMap<>();
        param.put("name", "DB_API_TESTQQQ");
        param.put("description", "SSS");
        try {
            String result = post(CREATE_ORGANIZATION_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createWorkspace() {
        Map<String, String> param = new HashMap<>();
        param.put("name", "");
        param.put("description", "");
        //组织管理员可以不用设置组织，默认当前组织（设置无效）
        param.put("organizationId", "");
        try {
            String result = post(CREATE_WORKSPACE_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addRoleToUser() {
        Map<String, Object> param = new HashMap<>();
        //用户ID 唯一(登录标识) 必填
        param.put("roleId", "afb5b459-7b29-4087-9fea-a31213d2bf05");
        param.put("userId", "doveorg");

        //资源Id列表,系统管理员(不填)、组织管理员(组织ID列表，必填)、工作空间用户(工作空间ID，必填)
        List<String> resourceIds = new ArrayList<>();
        resourceIds.add("2cc5ef20-8697-4103-b3d7-e3f53b14a546");
        param.put("resourceIds", resourceIds);

        try {
            String result = post(ADD_ROLE_TO_USER_URL, JSON.toJSONString(param));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getOrganizationUserList() {
        try {
            String result = get(GET_ORGANIZATION_USER_LIST_URL);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getworkspaceUserList() {
        try {
            String result = get(GET_WORKSPACE_USER_LIST_URL);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String get(String url) throws Exception {
        HttpURLConnection connection = getConnection(url);
        if (connection == null) {
            throw new Exception("获取connection失败");
        }
        connection.setRequestMethod("GET");
        connection.connect();
        InputStream in = connection.getInputStream();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
        }
        bs.flush();
        return bs.toString("utf-8");
    }

    private static String post(String url, String param) throws Exception {
        HttpURLConnection connection = getConnection(url);
        if (connection == null) {
            throw new Exception("获取connection失败");
        }
        connection.setRequestMethod("POST");
        connection.connect();
        connection.getOutputStream().write(JSON.toJSONBytes(JSON.parseObject(param)));
        connection.getOutputStream().flush();
        InputStream in = connection.getInputStream();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
        }
        bs.flush();
        return bs.toString("utf-8");
    }

    private static HttpURLConnection getConnection(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestProperty("Accept", "application/json;charset=UTF-8");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("accessKey", accessKey);
            connection.setRequestProperty("signature", getSignature());
            connection.setRequestProperty("sourceId", sourceId);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getSignature() throws Exception {
        return aesEncrypt(accessKey + "|" + UUID.randomUUID() + "|" + System.currentTimeMillis(), secretKey, accessKey);
    }


    public static String aesEncrypt(String src, String secretKey, String iv) throws Exception {

        byte[] raw = secretKey.getBytes("UTF-8");

        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv1);

        byte[] encrypted = cipher.doFinal(src.getBytes("UTF-8"));

        return Base64.encodeBase64String(encrypted).replaceAll("[\\s*\t\n\r]", "");

    }

}


