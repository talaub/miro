package com.sirweb.miro.parsing.values.miro;

import java.util.HashMap;
import java.util.Map;

public class Color implements MiroValue {
    private int red, green, blue, alpha;

    public Color (String hex) {
        if (hex.length() == 4)
            hex = "#" + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2) + hex.charAt(3) + hex.charAt(3);
        java.awt.Color intern = java.awt.Color.decode(hex);

        red = intern.getRed();
        green = intern.getGreen();
        blue = intern.getBlue();
        alpha = intern.getAlpha();

    }

    public static Map<String, String> getDefaultColorDictionary () {
        Map<String, String> dict = new HashMap<String, String>();
        dict.put("aliceblue", "#f0f8ff");
        dict.put("antiquewhite", "#faebd7");
        dict.put("aqua", "#00ffff");
        dict.put("aquamarine", "#7fffd4");
        dict.put("azure", "#f0ffff");
        dict.put("beige", "#f5f5dc");
        dict.put("bisque", "#ffe4c4");
        dict.put("black", "#000000");
        dict.put("blanchedalmond", "#ffebcd");
        dict.put("blue", "#0000ff");
        dict.put("blueviolet", "#8a2be2");
        dict.put("brown", "#a52a2a");
        dict.put("burlywood", "#deb887");
        dict.put("cadetblue", "#5f9ea0");
        dict.put("chartreuse", "#7fff00");
        dict.put("chocolate", "#d2691e");
        dict.put("coral", "#ff7f50");
        dict.put("cornflowerblue", "#6495ed");
        dict.put("cornsilk", "#fff8dc");
        dict.put("crimson", "#dc143c");
        dict.put("cyan", "#00ffff");
        dict.put("darkblue", "#00008b");
        dict.put("darkcyan", "#008b8b");
        dict.put("darkgoldenrod", "#b8860b");
        dict.put("darkgray", "#a9a9a9");
        dict.put("darkgreen", "#006400");
        dict.put("darkgrey", "#a9a9a9");
        dict.put("darkkhaki", "#bdb76b");
        dict.put("darkmagenta", "#8b008b");
        dict.put("darkolivegreen", "#556b2f");
        dict.put("darkorange", "#ff8c00");
        dict.put("darkorchid", "#9932cc");
        dict.put("darkred", "#8b0000");
        dict.put("darksalmon", "#e9967a");
        dict.put("darkseagreen", "#8fbc8f");
        dict.put("darkslateblue", "#483d8b");
        dict.put("darkslategray", "#2f4f4f");
        dict.put("darkslategrey", "#2f4f4f");
        dict.put("darkturquoise", "#00ced1");
        dict.put("darkviolet", "#9400d3");
        dict.put("deeppink", "#ff1493");
        dict.put("deepskyblue", "#00bfff");
        dict.put("dimgray", "#696969");
        dict.put("dimgrey", "#696969");
        dict.put("dodgerblue", "#1e90ff");
        dict.put("firebrick", "#b22222");
        dict.put("floralwhite", "#fffaf0");
        dict.put("forestgreen", "#228b22");
        dict.put("fuchsia", "#ff00ff");
        dict.put("gainsboro", "#dcdcdc");
        dict.put("ghostwhite", "#f8f8ff");
        dict.put("gold", "#ffd700");
        dict.put("goldenrod", "#daa520");
        dict.put("gray", "#808080");
        dict.put("green", "#008000");
        dict.put("greenyellow", "#adff2f");
        dict.put("grey", "#808080");
        dict.put("honeydew", "#f0fff0");
        dict.put("hotpink", "#ff69b4");
        dict.put("indianred", "#cd5c5c");
        dict.put("indigo", "#4b0082");
        dict.put("ivory", "#fffff0");
        dict.put("khaki", "#f0e68c");
        dict.put("lavender", "#e6e6fa");
        dict.put("lavenderblush", "#fff0f5");
        dict.put("lawngreen", "#7cfc00");
        dict.put("lemonchiffon", "#fffacd");
        dict.put("lightblue", "#add8e6");
        dict.put("lightcoral", "#f08080");
        dict.put("lightcyan", "#e0ffff");
        dict.put("lightgoldenrodyellow", "#fafad2");
        dict.put("lightgray", "#d3d3d3");
        dict.put("lightgreen", "#90ee90");
        dict.put("lightgrey", "#d3d3d3");
        dict.put("lightpink", "#ffb6c1");
        dict.put("lightsalmon", "#ffa07a");
        dict.put("lightseagreen", "#20b2aa");
        dict.put("lightskyblue", "#87cefa");
        dict.put("lightslategray", "#778899");
        dict.put("lightslategrey", "#778899");
        dict.put("lightsteelblue", "#b0c4de");
        dict.put("lightyellow", "#ffffe0");
        dict.put("lime", "#00ff00");
        dict.put("limegreen", "#32cd32");
        dict.put("linen", "#faf0e6");
        dict.put("magenta", "#ff00ff");
        dict.put("maroon", "#800000");
        dict.put("mediumaquamarine", "#66cdaa");
        dict.put("mediumblue", "#0000cd");
        dict.put("mediumorchid", "#ba55d3");
        dict.put("mediumpurple", "#9370db");
        dict.put("mediumseagreen", "#3cb371");
        dict.put("mediumslateblue", "#7b68ee");
        dict.put("mediumspringgreen", "#00fa9a");
        dict.put("mediumturquoise", "#48d1cc");
        dict.put("mediumvioletred", "#c71585");
        dict.put("midnightblue", "#191970");
        dict.put("mintcream", "#f5fffa");
        dict.put("mistyrose", "#ffe4e1");
        dict.put("moccasin", "#ffe4b5");
        dict.put("navajowhite", "#ffdead");
        dict.put("navy", "#000080");
        dict.put("oldlace", "#fdf5e6");
        dict.put("olive", "#808000");
        dict.put("olivedrab", "#6b8e23");
        dict.put("orange", "#ffa500");
        dict.put("orangered", "#ff4500");
        dict.put("orchid", "#da70d6");
        dict.put("palegoldenrod", "#eee8aa");
        dict.put("palegreen", "#98fb98");
        dict.put("paleturquoise", "#afeeee");
        dict.put("palevioletred", "#db7093");
        dict.put("papayawhip", "#ffefd5");
        dict.put("peachpuff", "#ffdab9");
        dict.put("peru", "#cd853f");
        dict.put("pink", "#ffc0cb");
        dict.put("plum", "#dda0dd");
        dict.put("powderblue", "#b0e0e6");
        dict.put("purple", "#800080");
        dict.put("rebeccapurple", "#663399");
        dict.put("red", "#ff0000");
        dict.put("rosybrown", "#bc8f8f");
        dict.put("royalblue", "#4169e1");
        dict.put("saddlebrown", "#8b4513");
        dict.put("salmon", "#fa8072");
        dict.put("sandybrown", "#f4a460");
        dict.put("seagreen", "#2e8b57");
        dict.put("seashell", "#fff5ee");
        dict.put("sienna", "#a0522d");
        dict.put("silver", "#c0c0c0");
        dict.put("skyblue", "#87ceeb");
        dict.put("slateblue", "#6a5acd");
        dict.put("slategray", "#708090");
        dict.put("slategrey", "#708090");
        dict.put("snow", "#fffafa");
        dict.put("springgreen", "#00ff7f");
        dict.put("steelblue", "#4682b4");
        dict.put("tan", "#d2b48c");
        dict.put("teal", "#008080");
        dict.put("thistle", "#d8bfd8");
        dict.put("tomato", "#ff6347");
        dict.put("turquoise", "#40e0d0");
        dict.put("violet", "#ee82ee");
        dict.put("wheat", "#f5deb3");
        dict.put("white", "#ffffff");
        dict.put("whitesmoke", "#f5f5f5");
        dict.put("yellow", "#ffff00");
        dict.put("yellowgreen", "#9acd32");

        return dict;
    }

    public static boolean knowsColor (String colorName) {
        return getDefaultColorDictionary().containsKey(colorName);
    }

    public String toString () {
        if (alpha == 255) {
            String hex = String.format("#%02x%02x%02x", red, green, blue);
            if (getDefaultColorDictionary().containsValue(hex)) {
                for (String key : getDefaultColorDictionary().keySet())
                    if (hex.equals(getDefaultColorDictionary().get(key)))
                        return key;

            }
            else {
                if (hex.charAt(1) == hex.charAt(2)
                        && hex.charAt(3) == hex.charAt(4)
                        && hex.charAt(5) == hex.charAt(6))
                    return "#" + hex.charAt(1) + hex.charAt(3) + hex.charAt(5);
                else
                    return hex;
            }
        }
        else
            return String.format("rgba(%i, %i, %i, %d)", red, green, blue, alpha / 255.0);

        return "transparent";
    }
}
