package com.sougata.meditrack;

import java.util.ArrayList;
import java.util.List;

public class ShapeDropDownMenu {
    public static List<ShapeDropdownItem> getShapeList() {
        List<ShapeDropdownItem> shapeList = new ArrayList<>();
        shapeList.add(new ShapeDropdownItem("Capsule", R.drawable.ic_shape_capsule));
        shapeList.add(new ShapeDropdownItem("Cream", R.drawable.ic_shape_cream));
        shapeList.add(new ShapeDropdownItem("Drops", R.drawable.ic_shape_drops));
        shapeList.add(new ShapeDropdownItem("Inhaler", R.drawable.ic_shape_inhaler));
        shapeList.add(new ShapeDropdownItem("Injection", R.drawable.ic_shape_injection));
        shapeList.add(new ShapeDropdownItem("Liquid", R.drawable.ic_shape_liquid));
        shapeList.add(new ShapeDropdownItem("Spray", R.drawable.ic_shape_spray));
        shapeList.add(new ShapeDropdownItem("Syrup", R.drawable.ic_shape_syrup));
        shapeList.add(new ShapeDropdownItem("Tablet", R.drawable.ic_shape_tablet));
        return shapeList;
    }
}
