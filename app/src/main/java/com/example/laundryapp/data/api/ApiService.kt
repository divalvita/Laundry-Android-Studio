package com.example.laundryapp.data.api

import com.example.laundryapp.data.model.*
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.GET

interface ApiService {

    // =========================================
    // USERS
    // =========================================

    @GET("users/")
    fun getUsers(): Call<List<UserResponse>>

    @GET("users/{user_id}")
    fun getUserById(@Path("user_id") userId: Int): Call<UserResponse>

    @POST("users/")
    fun createUser(@Body request: UserRequest): Call<UserResponse>
    @PUT("users/{user_id}")
    fun updateUser(
        @Path("user_id") userId: Int,
        @Body request: UserUpdateRequest
    ): Call<UserResponse>


    // =========================================
    // CUSTOMERS
    // =========================================

    @GET("customers/")
    fun getCustomers(): Call<List<CustomerResponse>>

    @POST("customers/")
    fun createCustomer(@Body customer: CustomerRequest): Call<CustomerResponse>

    @PUT("customers/{customer_id}")
    fun updateCustomer(
        @Path("customer_id") customerId: Int,
        @Body customer: CustomerRequest
    ): Call<CustomerResponse>

    @DELETE("customers/{customer_id}")
    fun deleteCustomer(@Path("customer_id") customerId: Int): Call<Unit>


    // =========================================
    // CATEGORIES
    // =========================================

    @GET("categories/")
    fun getCategories(): Call<List<CategoryResponse>>


    // =========================================
    // SERVICES
    // =========================================

    @GET("services/")
    fun getServices(): Call<List<ServiceResponse>>

    @GET("services/{service_id}")
    fun getServiceById(@Path("service_id") serviceId: Int): Call<ServiceResponse>

    @POST("services/")
    fun createService(@Body service: ServiceRequest): Call<ServiceResponse>

    @PUT("services/{service_id}")
    fun updateService(
        @Path("service_id") serviceId: Int,
        @Body service: ServiceRequest
    ): Call<ServiceResponse>

    @DELETE("services/{service_id}")
    fun deleteService(@Path("service_id") serviceId: Int): Call<Unit>


    // =========================================
    // ORDERS
    // =========================================

    @GET("orders/")
    fun getOrders(): Call<List<OrderResponse>>

    @GET("orders/{order_id}")
    fun getOrderById(@Path("order_id") orderId: Int): Call<OrderResponse>

    @POST("orders/")
    fun createOrder(@Body order: OrderRequest): Call<OrderResponse>

    @PUT("orders/{order_id}")
    fun updateOrderStatus(
        @Path("order_id") orderId: Int,
        @Body orderUpdate: OrderUpdateRequest
    ): Call<OrderResponse>

    @DELETE("orders/{order_id}")
    fun deleteOrder(@Path("order_id") orderId: Int): Call<Unit>


    // ================= PAYMENTS =================

    @GET("payments/")
    fun getPayments(): Call<List<PaymentResponse>>

    @POST("payments/")
    fun createPayment(
        @Body payment: PaymentRequest
    ): Call<PaymentResponse>


    // =========================================
    // EXPENSES
    // =========================================

    @GET("expenses/")
    fun getExpenses(): Call<List<ExpenseResponse>>

    @POST("expenses/")
    fun createExpense(@Body expense: ExpenseRequest): Call<ExpenseResponse>


    // =========================================
    // NOTIFICATIONS
    // =========================================

    @GET("notifications/")
    fun getNotifications(): Call<List<NotificationResponse>>

    @POST("notifications/")
    fun createNotification(
        @Body notification: NotificationRequest
    ): Call<NotificationResponse>

    @DELETE("notifications/{notification_id}")
    fun deleteNotification(
        @Path("notification_id") notificationId: Int
    ): Call<Unit>

    // =========================================
// DASHBOARD
// =========================================

    @GET("dashboard/")
    fun getDashboard(): Call<DashboardResponse>
    @GET("/")
    fun checkApi(): Call<Map<String, Any>>
}