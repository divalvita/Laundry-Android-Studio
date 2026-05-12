package com.example.laundryapp.network

import com.example.laundryapp.model.Customer
import com.example.laundryapp.model.CustomerRequest
import com.example.laundryapp.model.Service
import com.example.laundryapp.model.ServiceRequest
import retrofit2.Call
import retrofit2.http.*
import com.example.laundryapp.model.Order
import com.example.laundryapp.model.OrderRequest

interface ApiService {

    @GET("/")
    fun checkApi(): Call<Map<String, Any>>

    // CUSTOMER
    @GET("customers/")
    fun getCustomers(): Call<List<Customer>>

    @POST("customers/")
    fun createCustomer(@Body customer: CustomerRequest): Call<Customer>

    @PUT("customers/{customer_id}")
    fun updateCustomer(
        @Path("customer_id") customerId: Int,
        @Body customer: CustomerRequest
    ): Call<Customer>

    @DELETE("customers/{customer_id}")
    fun deleteCustomer(@Path("customer_id") customerId: Int): Call<Void>

    // SERVICE
    @GET("services/")
    fun getServices(): Call<List<Service>>

    @POST("services/")
    fun createService(@Body service: ServiceRequest): Call<Service>

    @PUT("services/{service_id}")
    fun updateService(
        @Path("service_id") serviceId: Int,
        @Body service: ServiceRequest
    ): Call<Service>

    @DELETE("services/{service_id}")
    fun deleteService(@Path("service_id") serviceId: Int): Call<Void>
    @GET("orders/")
    fun getOrders(): Call<List<Order>>

    @POST("orders/")
    fun createOrder(
        @Body order: OrderRequest
    ): Call<Order>
}