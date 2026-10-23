/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.collectionsai.workbench;

import static cn.zhuatech.collectionsai.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);
    keys(r, "asOf", "invoices");
    var asOf = date(text(r, "asOf", 10));
    Set<String> ids = new HashSet<>();
    Map<String, BigDecimal> totals = new LinkedHashMap<>();
    for (String b : List.of("NOT_DUE", "D1_30", "D31_60", "D61_90", "D91_PLUS"))
      totals.put(b, BigDecimal.ZERO);
    BigDecimal disputed = BigDecimal.ZERO;
    List<Map<String, Object>> out = new ArrayList<>();
    for (var i : rows(r, "invoices", 1, 500)) {
      keys(i, "id", "dueDate", "amount", "applied", "disputed");
      String id = id(i, "id");
      unique(ids, id);
      var due = date(text(i, "dueDate", 10));
      var amount = money(i, "amount");
      var applied = money(i, "applied");
      if (applied.compareTo(amount) > 0) throw new IllegalArgumentException("核销金额不能超过应收金额");
      var outstanding = amount.subtract(applied);
      long age = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(due, asOf));
      String bucket =
          age == 0
              ? "NOT_DUE"
              : age <= 30 ? "D1_30" : age <= 60 ? "D31_60" : age <= 90 ? "D61_90" : "D91_PLUS";
      boolean dispute = flag(i, "disputed");
      totals.merge(bucket, outstanding, BigDecimal::add);
      if (dispute) disputed = disputed.add(outstanding);
      out.add(
          Map.of(
              "id",
              id,
              "ageDays",
              age,
              "bucket",
              bucket,
              "outstanding",
              outstanding,
              "disputed",
              dispute));
    }
    List<Map<String, Object>> buckets = new ArrayList<>();
    totals.forEach((k, v) -> buckets.add(Map.of("bucket", k, "amount", v)));
    return Map.of("buckets", buckets, "disputedBalance", disputed, "invoices", out);
  }
}
