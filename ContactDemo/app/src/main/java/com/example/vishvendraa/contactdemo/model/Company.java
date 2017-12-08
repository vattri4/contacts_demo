package com.example.vishvendraa.contactdemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Company implements Serializable {

@SerializedName("name")
@Expose
private String name;
@SerializedName("catchPhrase")
@Expose
private String catchPhrase;
@SerializedName("bs")
@Expose
private String bs;

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getCatchPhrase() {
return catchPhrase;
}

public void setCatchPhrase(String catchPhrase) {
this.catchPhrase = catchPhrase;
}

public String getBs() {
return bs;
}

public void setBs(String bs) {
this.bs = bs;
}

}