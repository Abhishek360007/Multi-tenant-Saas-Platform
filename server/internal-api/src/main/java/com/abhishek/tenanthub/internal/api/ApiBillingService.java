package com.abhishek.tenanthub.internal.api;

/**
 * Subscription plan limits for the current tenant.
 *
 * @author Abhishek Raj
 */
public interface ApiBillingService {

    /**
     * Throws a business error if the tenant's plan does not allow another user.
     */
    void checkCanAddUser();

    /**
     * Throws a business error if the tenant's plan does not allow another project.
     */
    void checkCanAddProject();
}
