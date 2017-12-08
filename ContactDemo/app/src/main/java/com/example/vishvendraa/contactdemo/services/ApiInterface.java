package com.example.vishvendraa.contactdemo.services;

import com.example.vishvendraa.contactdemo.model.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by vishvendraa on 11/8/2017.
 */
public interface ApiInterface {
    @GET("users")
   Call<List<Contact>> getContactList();
}