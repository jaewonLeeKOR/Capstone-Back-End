package com.inha.capstone.domain;

public enum ApplicationCategory {

    ETC("기타");
    private final String name;

    ApplicationCategory (String name){
        this.name =name;
    }

    public String getName(){ return this.name; }

    public static ApplicationCategory nameOf(String name){
        for(ApplicationCategory applicationCategory : ApplicationCategory.values()){
            if(applicationCategory.getName().equals(name)){
                return applicationCategory;
            }
        }
        throw new IllegalArgumentException();
    }
}
