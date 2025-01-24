package com.sougata.meditrack;

import java.util.ArrayList;
import java.util.List;

public class ShapeDropDownMenu {
    public static int[] shapes = {
            R.drawable.ic_shape_capsule,
            R.drawable.ic_shape_cream,
            R.drawable.ic_shape_drops,
            R.drawable.ic_shape_inhaler,
            R.drawable.ic_shape_injection,
            R.drawable.ic_shape_liquid,
            R.drawable.ic_shape_spray,
            R.drawable.ic_shape_syrup,
            R.drawable.ic_shape_tablet
    };
    public static String[] shapeNames = {
            "Capsule",
            "Cream",
            "Drops",
            "Inhaler",
            "Injection",
            "Liquid",
            "Spray",
            "Syrup",
            "Tablet"
    };
    public static List<ShapeDropdownItem> getShapeList() {
        List<ShapeDropdownItem> shapeList = new ArrayList<>();
        for (int i = 0; i < shapeNames.length; i++) {
            shapeList.add(new ShapeDropdownItem(shapeNames[i], shapes[i]));
        }
        return shapeList;
    }
}
