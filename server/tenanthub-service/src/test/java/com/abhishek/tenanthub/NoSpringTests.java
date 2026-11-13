package com.abhishek.tenanthub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.common.consts.ConfigDataConst;
import com.abhishek.tenanthub.common.consts.JwtConst;
import com.abhishek.tenanthub.common.consts.RegexConst;
import com.abhishek.tenanthub.common.util.*;
import com.abhishek.tenanthub.internal.dto.OperateUserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import lombok.*;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.SelectUtils;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abhishek.tenanthub.common.consts.LoginConst.*;
import static com.abhishek.tenanthub.common.util.IpUtils.getLocalIPS;


public class NoSpringTests {

    @Test
    public void test1() {
        boolean after = LocalDateTime.now().isAfter(LocalDateTime.of(LocalDate.of(2023, 12, 28), LocalTime.MAX));
        System.out.println(after);
    }


    @Test
    public void test3() {
        System.out.println(FileUtil.extName("https://example.com/base/63b7df8b9e8581f5bff107e2.jpg"));
    }


    @Test
    public void test4() {
        boolean isMatch = Pattern.matches("\\w{4,12}$", "Hello there dwa541");
        System.out.println(isMatch);
    }


    @Test
    public void test5() {
        byte[] bytes = IdUtil.getSnowflakeNextIdStr().getBytes();
        System.out.println(new String(bytes));
        System.out.println(bytes.length);
    }


    @Test
    public void test6() {
        var treeNodes = List.of(
                new TreeNode<>(1001, 10, "Department 1", 1),
                new TreeNode<>(1002, 11, "Department 2", 3),
                new TreeNode<>(1003, 1002, "Department 2", 4),
                new TreeNode<>(1004, 1002, "Department 2", 5),
                new TreeNode<>(1005, 1001, "Department 2", 6)
        );
        var first = treeNodes.stream().map(TreeNode::getParentId).sorted().findFirst().get();
        List<Tree<Integer>> build = TreeUtil.build(treeNodes, first);
        System.out.println(JsonUtils.toFormatJson(build));
    }


    @Test
    public void test7() {
        var l = Duration.ofMinutes(1).toMillis();
        System.out.println(l);
    }


    @Test
    public void test8() {
        var port = 19222;
        getLocalIPS().stream()
                .map(ip -> String.format("http://%s:%d/doc.html", ip, port))
                .forEach(System.out::println);
    }


    @Test
    public void test9() {
        System.out.println(JwtSecurityUtils.encryptPassword("test123456"));
        System.out.println(JwtSecurityUtils.encryptPassword("test456789"));
    }


    @Test
    public void test10() throws Exception {
        var ds1 = new MysqlDataSource();
        ds1.setURL("jdbc:mysql://192.168.0.188:3306/hello1?characterEncoding=utf8&useSSL=false");
        ds1.setUser("test123");
        ds1.setPassword("mht123456");
        var liquibase1 = new MultiTenantSpringLiquibase();
        liquibase1.setDataSource(ds1);
        liquibase1.setResourceLoader(new FileSystemResourceLoader());
        liquibase1.setChangeLog("C:\\Users\\demo\\code\\javaProjects\\tenanthub\\server\\tenanthub-service\\src\\main\\resources\\db\\changelog-other.xml");
        liquibase1.setSchemas(List.of("hello1", "hello2"));
        liquibase1.afterPropertiesSet();
    }


    @Test
    public void test11() throws Exception {
        // The login endpoint also needs the tenant ID
        var whiteList = Stream.of(JwtConst.WHITE_LIST, JwtConst.TENANT_WHITE_LIST)
                .flatMap(Stream::of)
                .filter(s -> !JwtConst.LOGIN_URL.equals(s))
                .distinct()
                .toList();
        String formatJson = JsonUtils.toFormatJson(Map.of(
                "data", whiteList,
                "time", LocalTime.now()
        ));
        System.out.println(formatJson);
    }


    @Getter
    @Setter
    @Builder
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestExportEntity implements java.io.Serializable {

        @Schema(description = "ID")
        private Long id;

        @Schema(description = "Name")
        private String name;

        @Schema(description = "Hobby")
        protected List<String> hobby;

        @Schema(description = "Elder")
        protected Map<String, Long> elder;

        @Schema(description = "Created on")
        protected LocalDate createDate;

        @Schema(description = "Created at")
        protected LocalTime createTime;

        @Schema(description = "Created at")
        protected LocalDateTime createDateTime;

        @Schema(description = "Creator details")
        protected OperateUserDTO createUserEntity;

    }


    @Test
    public void test12() throws IOException {
        List<String> source = List.of("A1", "B1", "C3", "D4", "E5", "F6", "S7");
        var dataList = new ArrayList<TestExportEntity>();
        for (int i = 0; i < 20000; i++) {
            dataList.add(
                    new TestExportEntity(
                            (long) (i + 1),
                            RandomUtil.randomString(20),
                            RandomUtil.randomEleList(source, 3),
                            Map.of(String.valueOf(new char[]{RandomUtil.randomChinese(), RandomUtil.randomChinese(), RandomUtil.randomChinese()}), RandomUtil.randomLong(40, 88)),
                            LocalDate.now(),
                            LocalTime.now(),
                            LocalDateTime.now(),
                            new OperateUserDTO(
                                    RandomUtil.randomLong(),
                                    "a",
                                    String.valueOf(new char[]{RandomUtil.randomChinese(), RandomUtil.randomChinese(), RandomUtil.randomChinese()}
                                    )
                            )
                    )
            );
        }
        var start = System.currentTimeMillis();
        var entity = WrapExcelUtils.genPageWrite(5000, dataList);
        var file = Files.createFile(Path.of(String.format("%s.xlsx", RandomUtil.randomString(10)))).toFile();
        FileUtil.writeBytes(entity.toByteArray(), file);
        var ms = System.currentTimeMillis() - start;
        System.out.printf("%s => Duration: %d ms \n", file.getName(), ms);
    }


    @Test
    public void test13() {
        int i = PageUtil.totalPage(112, 10);
        System.out.println(i);
    }


    @Test
    public void test14() {
        var ignoreContainsKeys = List.of(
                CAPTCHA_CODE_KEY,
                LOGIN_TOKEN_KEY,
                FORCED_OFFLINE_KEY,
                TenantUtils.X_TENANT_ID,
                ConfigDataConst.REDIS_CACHE_KEY,
                ConfigDataConst.DEFAULT_USER_PASSWORD.getKey(),
                ConfigDataConst.DEFAULT_TENANT_LOGO.getKey(),
                ConfigDataConst.DEFAULT_ROLE_NAME.getKey()
        );
        var str = "waeq" + IdUtil.nanoId();
        boolean b1 = ignoreContainsKeys.stream().noneMatch(s -> StrUtil.contains(str, s));
        boolean b2 = ignoreContainsKeys.stream().noneMatch(str::startsWith);
        boolean b3 = ignoreContainsKeys.stream().noneMatch(str::equals);
        System.out.printf("%s contains: %s\n", str, b1);
        System.out.printf("%s startsWith: %s\n", str, b2);
        System.out.printf("%s equals: %s\n", str, b3);
    }


    @Test
    public void test15() {
        var options = new IdGeneratorOptions((short) 63);
        YitIdHelper.setIdGenerator(options);
        for (int i = 0; i < 100; i++) {
            System.out.println(YitIdHelper.nextId());
        }
    }


    @Test
    public void test16() {
        var pattern = Pattern.compile(RegexConst.MOBILE);
        var matcher = pattern.matcher("9876500103");
        System.out.println(matcher.find());
    }


    @Test
    public void test17() {
        for (int i = 0; i < 10; i++) {
            System.out.println(IdUtils.getStandaloneId());
        }
    }


    @Test
    public void test18() throws IOException {
        System.out.println(IpUtils.getRealAddressByIP("115.151.120.216"));
    }


    @Test
    public void test19() throws Exception {
        // Whole table
        Table table = new Table("test");
        Select select = SelectUtils.buildSelectFromTable(table);
        System.err.println(select); // SELECT * FROM test

        // Select specific columns
        Select buildSelectFromTableAndExpressions = SelectUtils.buildSelectFromTableAndExpressions(new Table("test"), new Column("col1"), new Column("col2"));
        System.err.println(buildSelectFromTableAndExpressions); // SELECT col1, col2 FROM test

        // WHERE =
        EqualsTo equalsTo = new EqualsTo(); // Equals expression
        equalsTo.setLeftExpression(new Column(table, "user_id")); // Set the left-hand value of the expression
        equalsTo.setRightExpression(new StringValue("123456"));// Set the right-hand value of the expression
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody(); // Convert to a more specific Select object
        plainSelect.setWhere(equalsTo);
        System.err.println(plainSelect);//  SELECT * FROM test WHERE test.user_id = '123456'

        // WHERE  != <>
        NotEqualsTo notEqualsTo = new NotEqualsTo();
        notEqualsTo.setLeftExpression(new Column(table, "user_id")); // Set the left-hand value of the expression
        notEqualsTo.setRightExpression(new StringValue("123456"));// Set the right-hand value of the expression
        PlainSelect plainSelectNot = (PlainSelect) select.getSelectBody();
        plainSelectNot.setWhere(notEqualsTo);
        System.err.println(plainSelectNot);//  SELECT * FROM test WHERE test.user_id <> '123456'

        // For other operators, add expressions as above
        GreaterThan gt = new GreaterThan(); // ">"
        GreaterThanEquals geq = new GreaterThanEquals(); // ">="
        MinorThan mt = new MinorThan(); // "<"
        MinorThanEquals leq = new MinorThanEquals();// "<="
        IsNullExpression isNull = new IsNullExpression(); // "is null"
        isNull.setNot(true);// "is not null"
        LikeExpression nlike = new LikeExpression();
        nlike.setNot(true); // "not like"
        Between bt = new Between();
        bt.setNot(true);// "not between"

        // WHERE LIKE
        LikeExpression likeExpression = new LikeExpression(); // Create a LIKE expression
        likeExpression.setLeftExpression(new Column("username")); // Left side of the expression
        likeExpression.setRightExpression(new StringValue("Ra%")); // right-hand expression
        PlainSelect plainSelectLike = (PlainSelect) select.getSelectBody();
        plainSelectLike.setWhere(likeExpression);
        System.err.println(plainSelectLike); // SELECT * FROM test WHERE username LIKE 'Ra%'

        // WHERE IN
        Set<String> deptIds = Sets.newLinkedHashSet(); // Create the IN list items
        deptIds.add("0001");
        deptIds.add("0002");
        var itemsList = new ExpressionList(deptIds.stream().map(StringValue::new).collect(Collectors.toList())); // Convert the collection into the JSQLParser item list
        InExpression inExpression = new InExpression(new Column("dept_id "), itemsList); // Create the IN expression with the column and list
        PlainSelect plainSelectIn = (PlainSelect) select.getSelectBody();
        plainSelectIn.setWhere(inExpression);
        System.err.println(plainSelectIn); // SELECT * FROM test WHERE dept_id  IN ('0001', '0002')

        // WHERE BETWEEN AND
        Between between = new Between();
        between.setBetweenExpressionStart(new LongValue(18)); // Set the start value
        between.setBetweenExpressionEnd(new LongValue(30)); // Set the end value
        between.setLeftExpression(new Column("age")); // Set the left-hand expression, usually a column
        PlainSelect plainSelectBetween = (PlainSelect) select.getSelectBody();
        plainSelectBetween.setWhere(between);
        System.err.println(plainSelectBetween); // SELECT * FROM test WHERE age BETWEEN 18 AND 30

        //  WHERE AND All conditions must hold
        AndExpression andExpression = new AndExpression(); // AND Expression
        andExpression.setLeftExpression(equalsTo); // AND Left-hand expression
        andExpression.setRightExpression(between);  // AND Right-hand expression
        PlainSelect plainSelectAnd = (PlainSelect) select.getSelectBody();
        plainSelectAnd.setWhere(andExpression);
        System.err.println(plainSelectAnd); //  SELECT * FROM test WHERE test.user_id = '123456' AND age BETWEEN 18 AND 30

        //  WHERE OR At least one condition must hold
        OrExpression orExpression = new OrExpression();// OR Expression
        orExpression.setLeftExpression(equalsTo); // OR Left-hand expression
        orExpression.setRightExpression(between);  // OR Right-hand expression
        PlainSelect plainSelectOr = (PlainSelect) select.getSelectBody();
        plainSelectOr.setWhere(orExpression);
        System.err.println(plainSelectOr); // SELECT * FROM test WHERE test.user_id = '123456' OR age BETWEEN 18 AND 30

        // ORDER BY Sort order
        OrderByElement orderByElement = new OrderByElement(); // Create the ORDER BY element
        orderByElement.isAsc(); //  Ascending order
        orderByElement.setExpression(new Column("col01")); // Set the sort column
        PlainSelect plainSelectOrderBy = (PlainSelect) select.getSelectBody();
        plainSelectOrderBy.addOrderByElements(orderByElement);
        System.err.println(plainSelectOrderBy); // SELECT * FROM test WHERE test.user_id = '123456' OR age BETWEEN 18 AND 30 ORDER BY col01
    }


    @Test
    public void test20() throws Exception {
        // Whole table
        Table table = new Table("test");
        Select select = SelectUtils.buildSelectFromTable(table);
        System.err.println(select); // SELECT * FROM test

        EqualsTo equalsTo = new EqualsTo(); // Equals expression
        equalsTo.setLeftExpression(new Column(table, "user_id")); // Set the left-hand value of the expression
        equalsTo.setRightExpression(new StringValue("123456"));// Set the right-hand value of the expression

        final var in = CCJSqlParserUtil.parseExpression("u.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in (1,2) )");
        PlainSelect plainSelectIn = (PlainSelect) select.getSelectBody();
        plainSelectIn.setWhere(
                new AndExpression(equalsTo, new AndExpression(equalsTo, new AndExpression(equalsTo, in)))
        );
        System.err.println(plainSelectIn); // SELECT * FROM test WHERE dept_id  IN ('0001', '0002')


    }

    @Test
    public void test21() throws Exception {
        Select childSelect = SelectUtils.buildSelectFromTableAndExpressions(
                new Table("sys_role_dept"),
                new Column("dept_id")
        );
        Set<String> deptIds = Set.of("10001", "10002", "10003");
        var itemsList = new ExpressionList<>(deptIds.stream().map(StringValue::new).collect(Collectors.toList()));
        InExpression inExpression = new InExpression(new Column("dept_id "), itemsList);
        childSelect.getPlainSelect().setWhere(inExpression);
        System.err.println(childSelect);

        Select select = SelectUtils.buildSelectFromTable(
                new Table("sys_role")
        );

        EqualsTo equalsTo = new EqualsTo(); // Equals expression
        equalsTo.setLeftExpression(new Column("user_id")); // Set the left-hand value of the expression
        equalsTo.setRightExpression(childSelect);// Set the right-hand value of the expression

        select.getPlainSelect().setWhere(equalsTo);

        System.err.println(select.getPlainSelect());//  SELECT * FROM test WHERE test.user_id = '123456'

    }
}
