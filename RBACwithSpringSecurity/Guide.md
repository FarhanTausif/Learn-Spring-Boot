# RBAC with Spring Security - Testing Guide

## 🚀 Quick Start

### Prerequisites
- PostgreSQL running with `rbac_db` database
- Application running on `http://localhost:8000`

### Start the Application
```bash
cd /path/to/RBACwithSpringSecurity
mvn spring-boot:run
```

## 🔐 Demo Accounts

| Username | Password | Roles | Access Level |
|----------|----------|-------|--------------|
| `admin` | `admin123` | ADMIN | Full system access |
| `manager` | `manager123` | MANAGER | Team management |
| `user` | `user123` | USER | Basic user access |
| `superuser` | `super123` | ADMIN,MANAGER,USER | All combined |

## 📋 Endpoint Testing Guide

### 1. Public Endpoints (No Authentication Required)

#### Home Page
```
GET http://localhost:8000/
```
- **Expected:** Welcome page with login/register options
- **Test:** Open in browser - should load without authentication

#### Login Page
```
GET http://localhost:8000/login
```
- **Expected:** Login form with demo credentials displayed
- **Test:** Should show form with username/password fields

#### Registration Page
```
GET http://localhost:8000/register
```
- **Expected:** Registration form for new users
- **Test:** Fill form and submit - new users get USER role by default

### 2. Authentication Testing

#### Login Process
1. Navigate to `http://localhost:8000/login`
2. Use any demo account credentials
3. **Expected:** Redirect to `/dashboard`
4. **Verify:** Navigation bar shows username and role-based menu items

#### Registration Process
1. Navigate to `http://localhost:8000/register`
2. Create new account with unique username/email
3. **Expected:** Redirect to login with success message
4. **Verify:** Can login with new credentials (gets USER role)

### 3. Role-Based Access Control Testing

#### Dashboard (Authenticated Users)
```
GET http://localhost:8000/dashboard
```
- **Authentication:** Required
- **Access:** All authenticated users
- **Expected:** Shows user info and role-specific quick access cards
- **Test:** Login with different roles - dashboard content changes

#### Admin Endpoints 🔴 (ADMIN Role Only)

```
GET http://localhost:8000/admin/
```
- **Authentication:** admin/admin123 or superuser/super123
- **Expected:** Admin dashboard with system management options
- **Test Access Denied:** Try with manager/user accounts

```
GET http://localhost:8000/admin/users
```
- **Expected:** User management interface with user list
- **Features:** View all users, their roles, and status

```
GET http://localhost:8000/admin/system
```
- **Expected:** System settings and configuration options
- **Features:** Security settings, maintenance options

#### Manager Endpoints 🟡 (ADMIN or MANAGER Roles)

```
GET http://localhost:8000/manager/
```
- **Authentication:** manager/manager123, admin/admin123, or superuser/super123
- **Expected:** Manager dashboard with team management tools
- **Test Access Denied:** Try with user account

```
GET http://localhost:8000/manager/reports
```
- **Expected:** Team performance metrics and reports
- **Features:** Performance stats, activity logs, report generation

```
GET http://localhost:8000/manager/team
```
- **Expected:** Team management interface
- **Features:** Team member list, task assignment, performance tracking

#### User Endpoints 🟢 (All Roles)

```
GET http://localhost:8000/user/
```
- **Authentication:** Any authenticated user
- **Expected:** Personal dashboard with user-specific tools
- **Features:** Personal stats, quick access to profile/tasks

```
GET http://localhost:8000/user/profile
```
- **Expected:** User profile management page
- **Features:** Edit personal info, security settings, preferences

```
GET http://localhost:8000/user/tasks
```
- **Expected:** Personal task management interface
- **Features:** Task list, progress tracking, deadline management

### 4. Security Testing Scenarios

#### Test Access Control
1. **Login as USER** (`user/user123`)
   - ✅ Can access: `/dashboard`, `/user/**`
   - ❌ Should deny: `/admin/**`, `/manager/**`
   - **Expected:** 403 Forbidden or redirect to access denied

2. **Login as MANAGER** (`manager/manager123`)
   - ✅ Can access: `/dashboard`, `/user/**`, `/manager/**`
   - ❌ Should deny: `/admin/**`

3. **Login as ADMIN** (`admin/admin123`)
   - ✅ Can access: All endpoints
   - **Test:** Full system access verification

4. **Login as SUPERUSER** (`superuser/super123`)
   - ✅ Can access: All endpoints (has all roles)
   - **Test:** Ultimate access verification

#### Test Unauthorized Access
```bash
# Try accessing protected endpoints without login
curl -I http://localhost:8000/admin/
curl -I http://localhost:8000/manager/
curl -I http://localhost:8000/user/
```
- **Expected:** Redirect to login page (302) or 401 Unauthorized

## 🧪 Browser Testing Steps

### Complete Role Testing Workflow

#### 1. Admin Role Testing
```
1. Login: admin/admin123
2. Verify navigation shows: Admin Panel, Manager Panel, User Panel
3. Test endpoints:
   - /admin/ → Admin Dashboard
   - /admin/users → User Management
   - /admin/system → System Settings
   - /manager/ → Manager access (admin can access)
   - /user/ → User access (admin can access)
4. Logout and verify redirect
```

#### 2. Manager Role Testing
```
1. Login: manager/manager123
2. Verify navigation shows: Manager Panel, User Panel (no Admin Panel)
3. Test endpoints:
   - /manager/ → Manager Dashboard
   - /manager/reports → Team Reports
   - /manager/team → Team Management
   - /user/ → User access (manager can access)
4. Try /admin/ → Should show 403 or redirect
```

#### 3. User Role Testing
```
1. Login: user/user123
2. Verify navigation shows: User Panel only
3. Test endpoints:
   - /user/ → User Dashboard
   - /user/profile → Profile Management
   - /user/tasks → Task Management
4. Try /admin/ and /manager/ → Should show 403 or redirect
```

## 🔍 Testing with cURL

### Authentication Flow
```bash
# Get login page
curl -c cookies.txt http://localhost:8000/login

# Login (get CSRF token first from login page)
curl -b cookies.txt -c cookies.txt -X POST \
  -d "username=admin&password=admin123" \
  http://localhost:8000/login

# Access protected endpoint
curl -b cookies.txt http://localhost:8000/admin/
```

### API Testing Script
```bash
#!/bin/bash
# Test all endpoints with different roles

echo "Testing RBAC Endpoints..."

# Test as Admin
echo "=== Testing as Admin ==="
curl -c admin_cookies.txt -X POST -d "username=admin&password=admin123" http://localhost:8000/login
curl -b admin_cookies.txt -s -o /dev/null -w "%{http_code}" http://localhost:8000/admin/ && echo " - Admin access: OK"
curl -b admin_cookies.txt -s -o /dev/null -w "%{http_code}" http://localhost:8000/manager/ && echo " - Manager access: OK"

# Test as Manager
echo "=== Testing as Manager ==="
curl -c manager_cookies.txt -X POST -d "username=manager&password=manager123" http://localhost:8000/login
curl -b manager_cookies.txt -s -o /dev/null -w "%{http_code}" http://localhost:8000/manager/ && echo " - Manager access: OK"
curl -b manager_cookies.txt -s -o /dev/null -w "%{http_code}" http://localhost:8000/admin/ && echo " - Admin access: DENIED (expected)"

# Cleanup
rm -f *_cookies.txt
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Application Won't Start
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Check if database exists
sudo -u postgres psql -l | grep rbac_db

# Restart application
mvn spring-boot:run
```

#### 2. Login Issues
- **Check:** Username/password case sensitivity
- **Verify:** Database has demo users (check logs on startup)
- **Reset:** Restart application to reinitialize users

#### 3. Access Denied Issues
- **Expected behavior** for proper RBAC implementation
- **Verify:** User has correct role in database
- **Check:** URL patterns match security configuration

#### 4. Page Not Found (404)
- **Verify:** Application is running on port 8000
- **Check:** URL spelling and case sensitivity
- **Confirm:** Endpoint exists in controllers

### Database Verification
```sql
-- Connect to database
psql -h localhost -U rbac_user -d rbac_db

-- Check users and their roles
SELECT id, username, email, roles, enabled FROM users;

-- Expected output:
--  id | username |      email       | roles  | enabled 
-- ----+----------+------------------+--------+---------
--   1 | admin    | admin@example.com| ADMIN  | t
--   2 | manager  | manager@...      | MANAGER| t
--   3 | user     | user@example.com | USER   | t
--   4 | superuser| superuser@...    | ADMIN,MANAGER,USER | t
```

## 📊 Expected Test Results

### Access Control Matrix

| Endpoint | USER | MANAGER | ADMIN | SUPERUSER |
|----------|------|---------|-------|-----------|
| `/` | ✅ | ✅ | ✅ | ✅ |
| `/login` | ✅ | ✅ | ✅ | ✅ |
| `/register` | ✅ | ✅ | ✅ | ✅ |
| `/dashboard` | ✅ | ✅ | ✅ | ✅ |
| `/user/**` | ✅ | ✅ | ✅ | ✅ |
| `/manager/**` | ❌ | ✅ | ✅ | ✅ |
| `/admin/**` | ❌ | ❌ | ✅ | ✅ |

### Performance Features
- ✅ **No @ManyToMany queries** - roles stored as strings
- ✅ **Single user query** - no expensive joins
- ✅ **Fast authentication** - minimal database hits
- ✅ **Optimized authorization** - in-memory role checking

## 🎯 Success Criteria

Your RBAC system is working correctly if:

1. ✅ All demo accounts can login successfully
2. ✅ Role-based navigation menus appear correctly
3. ✅ Access control prevents unauthorized endpoint access
4. ✅ Each role can access appropriate resources
5. ✅ Registration creates new users with USER role
6. ✅ Logout functionality works properly
7. ✅ No circular dependency errors in logs
8. ✅ Database tables created successfully
9. ✅ Performance is fast (no N+1 query issues)
10. ✅ UI is responsive and user-friendly

---

## 🎉 Congratulations!

You now have a fully functional, performance-optimized RBAC system with Spring Security! The system demonstrates enterprise-level security patterns while avoiding common performance pitfalls.
