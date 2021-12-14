package com.blueveery.springrest2ts.tests.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    boolean isAdmin;
    List<String> roleList;
    Set<String> tagsSet;
    HashSet tagsHashSet;
    Date joinDate;
}