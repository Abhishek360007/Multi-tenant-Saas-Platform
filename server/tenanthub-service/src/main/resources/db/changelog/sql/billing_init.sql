-- Subscription plans. Prices in paise (₹1 = 100 paise), before GST. -1 = unlimited.
INSERT INTO `sys_plan` (`plan_code`, `name`, `description`, `monthly_price_paise`, `yearly_price_paise`, `max_users`, `max_projects`, `sort`, `status`) VALUES
('free',  'Free',  'For trying TenantHub with a small team', 0,      0,       3,  1, 1, 1),
('basic', 'Basic', 'For growing teams',                     99900,  999000,  25, 5, 2, 1),
('pro',   'Pro',   'Unlimited users and projects',          299900, 2999000, -1, -1, 3, 1);

-- Menus: "Plans" for platform admins (target 1), "Billing > Subscription" for tenant users (target 0)
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `perms`, `component`, `icon`, `path`, `sort`, `menu_type`, `visible`, `target`, `keep_alive`, `create_time`, `create_user`, `last_update_time`, `last_update_user`) VALUES
(10055, 'Plans',        10001, 'sys:plan',         'sys/plan/index', 'money', '/sys/plan',      9, 0, 1, 1, 0, '2026-11-13 00:00:00', 19980817, '2026-11-13 00:00:00', 19980817),
(10056, 'Billing',      0,     'billing',          'Layout',         'money', '/billing',       2, 0, 1, 0, 0, '2026-11-13 00:00:00', 19980817, '2026-11-13 00:00:00', 19980817),
(10057, 'Subscription', 10056, 'billing:subscription', 'billing/index', 'money', '/billing/index', 1, 0, 1, 0, 0, '2026-11-13 00:00:00', 19980817, '2026-11-13 00:00:00', 19980817);

-- Tenant admins get menus through their permission template ("All modules")
INSERT INTO `sys_template_menu` (`template_id`, `menu_id`) VALUES (10001, 10056), (10001, 10057);
