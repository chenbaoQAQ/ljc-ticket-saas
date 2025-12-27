package com.ljc.common.context;

public class AuthContext {

    private static final ThreadLocal<Long> COMPANY_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> EMPLOYEE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();
    private static final ThreadLocal<String> NAME = new ThreadLocal<>();

    /* ====== set ====== */
    public static void setCompanyId(Long companyId) {
        COMPANY_ID.set(companyId);
    }

    public static void setEmployeeId(Long employeeId) {
        EMPLOYEE_ID.set(employeeId);
    }

    public static void setRole(String role) {
        ROLE.set(role);
    }

    public static void setName(String name) {
        NAME.set(name);
    }

    /* ====== get ====== */
    public static Long getCompanyId() {
        return COMPANY_ID.get();
    }

    public static Long getEmployeeId() {
        return EMPLOYEE_ID.get();
    }

    public static String getRole() {
        return ROLE.get();
    }

    public static String getName() {
        return NAME.get();
    }

    /* ====== clear（非常重要） ====== */
    public static void clear() {
        COMPANY_ID.remove();
        EMPLOYEE_ID.remove();
        ROLE.remove();
        NAME.remove();
    }
}

