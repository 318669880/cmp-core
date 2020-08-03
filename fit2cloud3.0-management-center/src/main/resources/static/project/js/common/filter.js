ProjectApp.filter('userSource', function (Translator) {
    return function (input) {
        if (input === 'local') {
            return Translator.get("i18n_user_source_local")
        }
        if (input === 'extra') {
            return Translator.get("i18n_user_source_extra")
        }

        return input;
    }
});

ProjectApp.filter('roleType', function (Translator) {
    return function (input) {
        if (input === 'System') {
            return Translator.get("i18n_role_type_System")
        }
        if (input === 'Additional') {
            return Translator.get("i18n_role_type_custom")
        }
    }
});


ProjectApp.filter('roleParentType', function (Translator) {
    return function (input) {
        if (input === 'ADMIN') {
            return Translator.get('系统管理员')
        }
        if (input === 'ORGADMIN') {
            return Translator.get('组织管理员')
        }
        if (input === 'USER') {
            return Translator.get('工作空间用户')
        }
    }
});


