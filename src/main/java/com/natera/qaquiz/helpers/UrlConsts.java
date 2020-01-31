package com.natera.qaquiz.helpers;

public class UrlConsts {

    public static final String TRIANGLE = "/triangle";
    public static final String ID = "id";
    public static final String TRIANGLE_ID = String.format("/triangle/{%s}", ID);
    public static final String ALL = String.format("%s/all", TRIANGLE);
    public static final String PERIMETER = String.format("%s/{%s}/perimeter", TRIANGLE, ID);
    public static final String AREA = String.format("%s/{%s}/area", TRIANGLE, ID);
}
