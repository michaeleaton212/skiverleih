package skirental;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public final class HtmlTemplate {
    private HtmlTemplate() {}

    // -------------------------
    // Public API
    // -------------------------

    /** Render a file template with {{placeholders}} (values are HTML-escaped).
     *  CleanCode: small util, clear contract. */
    public static String renderFile(String path, Map<String, String> vars) {
        String tpl = read(path); // throws AppException on error
        return render(tpl, vars, true);
    }

    /** Convenience: invoice for a rental. Expects templates/invoice.html.
     *  CleanCode: single responsibility, readable mapping. */
    public static String invoice(Customer c, Ski s, Rental r) {
        // Determine today once
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate start = LocalDate.now();
        String dateStr = start.format(df);

        String priceFmt = NumberFormat.getNumberInstance(Locale.GERMANY).format(r.getTotalPrice());

        // Provide required keys for invoice.html
        return renderFile("templates/invoice.html", Map.of(
                "customerName", c.getName(),
                "rentalId",     r.getId(),
                "skiBrand",     s.getBrand(),
                "skiType",      s.getType().name(),
                "skiLength",    String.valueOf(s.getLengthCm()),
                "days",         String.valueOf(r.getDays()),
                "price",        priceFmt,
                "date",         dateStr
        ));
    }

    /** Convenience: return confirmation HTML. Uses templates/return.html if available.
     *  CleanCode: fallback strategy, predictable output. */
    public static String returnConfirmation(Customer c, Ski s, Rental r) {
        String priceFmt = NumberFormat.getNumberInstance(Locale.GERMANY).format(r.getTotalPrice());
        Map<String, String> vars = Map.of(
                "customerName", c.getName(),
                "customerEmail", c.getEmail() == null ? "" : c.getEmail(),
                "rentalId", r.getId(),
                "skiBrand", s.getBrand(),
                "skiType", s.getType().name(),
                "skiLength", String.valueOf(s.getLengthCm()),
                "days", String.valueOf(r.getDays()),
                "total", priceFmt,
                "status", r.getStatus().name(),
                "date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        );

        String tpl = tryRead("templates/return.html");
        if (tpl != null) {
            return render(tpl, vars, true);
        }

        // Fallback HTML (if no file present)
        String fallback = """
            <html>
              <body style="font-family:Arial,Helvetica,sans-serif">
                <h2>Return confirmed</h2>
                <p>Hello {{customerName}},</p>
                <p>we have recorded your return <b>{{rentalId}}</b>.</p>
                <table cellpadding="6" cellspacing="0" border="0" style="border:1px solid #eee">
                  <tr><td><b>Customer</b></td><td>{{customerName}} ({{customerEmail}})</td></tr>
                  <tr><td><b>Ski</b></td><td>{{skiBrand}} {{skiType}}, {{skiLength}} cm</td></tr>
                  <tr><td><b>Days</b></td><td>{{days}}</td></tr>
                  <tr><td><b>Total</b></td><td>CHF {{total}}</td></tr>
                  <tr><td><b>Status</b></td><td>{{status}}</td></tr>
                  <tr><td><b>Date</b></td><td>{{date}}</td></tr>
                </table>
                <p>Thank you and see you again!</p>
              </body>
            </html>
            """;
        return render(fallback, vars, true);
    }

    // -------------------------
    // Internals
    // -------------------------

    /** Replace {{key}} with (optionally escaped) values.
     *  CleanCode: small pure function, predictable replacement. */
    private static String render(String template, Map<String, String> vars, boolean escapeValues) {
        String html = template;
        for (Map.Entry<String, String> e : vars.entrySet()) {
            String key = "{{" + e.getKey() + "}}";
            String val = e.getValue() == null ? "" : e.getValue();
            html = html.replace(key, escapeValues ? htmlEscape(val) : val);
        }
        return html;
    }

    /** Read template file from several common locations.
     *  CleanCode: clear failure mode (throws AppException). */
    private static String read(String path) {
        String s = tryRead(path);
        if (s == null) {
            s = tryRead(Path.of("src", path).toString());
        }
        if (s == null) {
            s = tryRead(Path.of("src", "main", "resources", path).toString());
        }
        if (s == null) {
            throw new AppException("Template not readable: " + path);
        }
        return s;
    }

    /** Try to read file; return null if not available.
     *  CleanCode: explicit null to signal missing optional template. */
    private static String tryRead(String path) {
        try {
            Path p = Path.of(path);
            if (Files.exists(p)) {
                return Files.readString(p, StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) { }
        return null;
    }

    /** Minimal HTML-escaping for values.
     *  CleanCode: small pure util, well tested area candidate. */
    private static String htmlEscape(String s) {
        String out = s;
        out = out.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        out = out.replace("\"", "&quot;");
        out = out.replace("'", "&#39;");
        return out;
    }
}
