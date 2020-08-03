/**
 * FIT2CLOUD material palette 生成器
 */
(function (global, factory) {
    if (global.Palette === undefined) {
        global.Palette = new factory();
    }
})(this, function () {
    // 主色调, hex基于500的颜色
    this.primary = function (hex, defaultColor) {
        hex = hex.trim();
        var hsl = hexToHSL(hex);
        var h = Math.round(hsl.h);
        var s = Math.round(hsl.s);
        var l = Math.round(hsl.l);

        return {
            '50': hslToHex({h: h, s: value(s - 4), l: value(l + 46)}),
            '100': hslToHex({h: h, s: value(s - 4), l: value(l + 36)}),
            '200': hslToHex({h: h, s: value(s - 4), l: value(l + 26)}),
            '300': hslToHex({h: h, s: value(s - 4), l: value(l + 12)}),
            '400': hslToHex({h: h, s: value(s - 4), l: value(l + 6)}),
            '500': hex,
            '600': hslToHex({h: h, s: value(s + 2), l: value(l - 6)}),
            '700': hslToHex({h: h, s: value(s + 6), l: value(l - 12)}),
            '800': hslToHex({h: h, s: value(s + 10), l: value(l - 18)}),
            '900': hslToHex({h: h, s: value(s + 14), l: value(l - 24)}),
            'A100': hslToHex({h: h, s: 100, l: 75}),
            'A200': hslToHex({h: h, s: 100, l: 65}),
            'A400': hslToHex({h: h, s: 99, l: 62}),
            'A700': hslToHex({h: h, s: 99, l: 58}),
            'contrastDefaultColor': defaultColor || "light"
        }
    };
    // 辅色调, hex基于200的颜色
    this.accent = function (hex, defaultColor) {
        hex = hex.trim();
        var hsl = hexToHSL(hex);
        var h = Math.round(hsl.h);
        var s = Math.round(hsl.s);
        var l = Math.round(hsl.l);

        return {
            '50': hslToHex({h: h, s: s, l: value(l + 20)}),
            '100': hslToHex({h: h, s: s, l: value(l + 10)}),
            '200': hex,
            '300': hslToHex({h: h, s: s, l: value(l - 14)}),
            '400': hslToHex({h: h, s: s, l: value(l - 20)}),
            '500': hslToHex({h: h, s: value(s + 4), l: value(l - 26)}),
            '600': hslToHex({h: h, s: value(s + 6), l: value(l - 32)}),
            '700': hslToHex({h: h, s: value(s + 10), l: value(l - 38)}),
            '800': hslToHex({h: h, s: value(s + 14), l: value(l - 42)}),
            '900': hslToHex({h: h, s: value(s + 18), l: value(l - 46)}),
            'A100': hslToHex({h: h, s: 100, l: 75}),
            'A200': hslToHex({h: h, s: 100, l: 65}),
            'A400': hslToHex({h: h, s: 99, l: 62}),
            'A700': hslToHex({h: h, s: 99, l: 58}),
            'contrastDefaultColor': defaultColor || "light"
        }
    };

    function hexToHSL(hex) {
        var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);

        var r = parseInt(result[1], 16);
        var g = parseInt(result[2], 16);
        var b = parseInt(result[3], 16);

        r /= 255;
        g /= 255;
        b /= 255;
        var max = Math.max(r, g, b), min = Math.min(r, g, b);
        var h, s, l = (max + min) / 2;

        if (max === min) {
            h = s = 0;
        } else {
            var d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            switch (max) {
                case r:
                    h = (g - b) / d + (g < b ? 6 : 0);
                    break;
                case g:
                    h = (b - r) / d + 2;
                    break;
                case b:
                    h = (r - g) / d + 4;
                    break;
            }
            h /= 6;
        }

        s = s * 100;
        s = Math.round(s);
        l = l * 100;
        l = Math.round(l);
        h = Math.round(360 * h);
        return {
            h: h, s: s, l: l
        };
    }

    function hslToHex(hsl) {
        var h = hsl.h, s = hsl.s, l = hsl.l;
        h /= 360;
        s /= 100;
        l /= 100;
        var r, g, b;
        if (s === 0) {
            r = g = b = l; // achromatic
        } else {
            var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            var p = 2 * l - q;
            r = hueToRGB(p, q, h + 1 / 3);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1 / 3);
        }

        return "#" + toHex(r) + toHex(g) + toHex(b);
    }

    function hueToRGB(p, q, t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1 / 6) return p + (q - p) * 6 * t;
        if (t < 1 / 2) return q;
        if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
        return p;
    }

    function toHex(x) {
        var hex = Math.round(x * 255).toString(16);
        return hex.length === 1 ? '0' + hex : hex;
    }

    function value(val) {
        return Math.min(100, Math.max(0, val));
    }
});