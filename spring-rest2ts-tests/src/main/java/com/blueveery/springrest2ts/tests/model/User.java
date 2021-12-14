package com.blueveery.springrest2ts.tests.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
    boolean isAdmin;
    List<String> roleList;
    Set<String> tagsSet;
    HashSet tagsHashSet;
    Date joinDate;
    Map<String, String> tagsMap;
    Map<String, Integer> numbersMap;
    HashMap<String, Date> datesMap;
}