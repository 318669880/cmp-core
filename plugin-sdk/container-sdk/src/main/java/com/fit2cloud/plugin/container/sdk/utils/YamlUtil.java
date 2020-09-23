package com.fit2cloud.plugin.container.sdk.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Map;
import java.util.Set;

public class YamlUtil extends Representer {
    @Override
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        MappingNode node = super.representJavaBean(properties, javaBean);
        node.setTag(Tag.MAP);
        return node;
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        if (propertyValue == null) {
            return null;
        } else {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            Node valueNode = tuple.getValueNode();
            if (Tag.NULL.equals(valueNode.getTag())) {
                return null;
            }
            if (valueNode instanceof CollectionNode) {
                if (((CollectionNode) valueNode).getValue().isEmpty()) {
                    return null;
                }
            }
            return tuple;
        }
    }

    public static String toYamlString(Object object) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YamlUtil yamlUtil = new YamlUtil();
        return new Yaml(yamlUtil, options).dump(object);
    }

    public static Map<String, Object> toMap(String yaml) {
        return new Yaml().load(yaml);
    }
}
