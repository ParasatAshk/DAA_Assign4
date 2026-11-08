package experiment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;

public class PlotGenerator {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 560;
    private static final int SCALE = 2;
    private static final int LEFT = 86;
    private static final int RIGHT = 120;
    private static final int TOP = 92;
    private static final int BOTTOM = 66;
    private static final int MARKER = 10;
    private static final int LABEL_GAP = 18;

    private static final Color SURFACE = new Color(0xfcfcfb);
    private static final Color PRIMARY_TEXT = new Color(0x0b0b0b);
    private static final Color SECONDARY_TEXT = new Color(0x52514e);
    private static final Color GRID = new Color(0xe6e5e0);
    private static final Color AXIS = new Color(0xb5b4ad);
    private static final Color[] SERIES_COLORS = {
        new Color(0x2a78d6), new Color(0xeb6834), new Color(0x1baf7a)
    };

    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 19);
    private static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font TICK_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");
        Files.createDirectories(Path.of("results/plots"));

        drawChart(readSeries("results/dp.csv", null, "algorithm", "n"),
            "LCS and Edit Distance: execution time", "String length n (both strings)", "dp_time.png");
        drawMatching("random_present", "random text, pattern present");
        drawMatching("random_absent", "random text, pattern absent");
        drawMatching("repetitive_present", "repetitive text, pattern present");
        drawMatching("repetitive_absent", "repetitive text, pattern absent");
        drawChart(readSeries("results/kasai.csv", null, "input_type", "n"),
            "Kasai LCP: execution time", "String length n", "kasai_time.png");
    }

    private static void drawMatching(String scenario, String description) throws IOException {
        drawChart(readSeries("results/matching.csv", scenario, "algorithm", "text_length"),
            "String matching: " + description, "Text length n", "matching_" + scenario + ".png");
    }

    private static Map<String, TreeMap<Double, Double>> readSeries(String file, String scenario, String seriesColumn, String xColumn) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(file));
        List<String> header = Arrays.asList(lines.get(0).split(","));
        Map<String, TreeMap<Double, Double>> series = new LinkedHashMap<>();

        for (String line : lines.subList(1, lines.size())) {
            String[] cells = line.split(",");
            if (scenario != null && !cells[header.indexOf("scenario")].equals(scenario)) {
                continue;
            }
            String name = cells[header.indexOf(seriesColumn)];
            double x = Double.parseDouble(cells[header.indexOf(xColumn)]);
            double y = Double.parseDouble(cells[header.indexOf("avg_ms")]);
            series.computeIfAbsent(name, key -> new TreeMap<>()).put(x, y);
        }
        return series;
    }

    private static void drawChart(Map<String, TreeMap<Double, Double>> series, String title, String xLabel, String fileName) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH * SCALE, HEIGHT * SCALE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.scale(SCALE, SCALE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(SURFACE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        double maxX = 0;
        double maxY = 0;
        for (TreeMap<Double, Double> points : series.values()) {
            maxX = Math.max(maxX, points.lastKey());
            for (double y : points.values()) {
                maxY = Math.max(maxY, y);
            }
        }
        double step = niceStep(maxY / 5);
        double topY = Math.ceil(maxY / step) * step;

        g.setColor(PRIMARY_TEXT);
        g.setFont(TITLE_FONT);
        g.drawString(title, LEFT, 34);

        drawAxes(g, series.values().iterator().next().keySet(), maxX, topY, step, xLabel);
        drawLegend(g, series.keySet());

        int index = 0;
        List<int[]> labels = new ArrayList<>();
        List<String> labelNames = new ArrayList<>();
        for (Map.Entry<String, TreeMap<Double, Double>> entry : series.entrySet()) {
            drawLine(g, entry.getValue(), maxX, topY, index);
            labels.add(new int[] {toPixelY(entry.getValue().lastEntry().getValue(), topY), labels.size()});
            labelNames.add(entry.getKey());
            index++;
        }
        drawEndLabels(g, labels, labelNames);

        g.dispose();
        ImageIO.write(image, "png", new File("results/plots/" + fileName));
    }

    private static void drawAxes(Graphics2D g, Iterable<Double> xValues, double maxX, double topY, double step, String xLabel) {
        int plotBottom = HEIGHT - BOTTOM;
        g.setFont(TICK_FONT);
        g.setStroke(new BasicStroke(1f));

        int tickCount = (int) Math.round(topY / step);
        for (int i = 0; i <= tickCount; i++) {
            double value = i * step;
            int y = toPixelY(value, topY);
            g.setColor(i == 0 ? AXIS : GRID);
            g.drawLine(LEFT, y, WIDTH - RIGHT, y);
            g.setColor(SECONDARY_TEXT);
            String text = formatTick(value, step);
            g.drawString(text, LEFT - 10 - g.getFontMetrics().stringWidth(text), y + 4);
        }

        for (double x : xValues) {
            int pixel = toPixelX(x, maxX);
            g.setColor(AXIS);
            g.drawLine(pixel, plotBottom, pixel, plotBottom + 5);
            g.setColor(SECONDARY_TEXT);
            String text = String.format(Locale.US, "%,d", (long) x);
            g.drawString(text, pixel - g.getFontMetrics().stringWidth(text) / 2, plotBottom + 20);
        }

        g.setFont(LABEL_FONT);
        g.setColor(SECONDARY_TEXT);
        g.drawString(xLabel, LEFT + (WIDTH - LEFT - RIGHT - g.getFontMetrics().stringWidth(xLabel)) / 2, HEIGHT - 16);

        Graphics2D rotated = (Graphics2D) g.create();
        rotated.translate(24, TOP + (HEIGHT - TOP - BOTTOM) / 2);
        rotated.rotate(-Math.PI / 2);
        String yLabel = "Average time (ms)";
        rotated.drawString(yLabel, -g.getFontMetrics().stringWidth(yLabel) / 2, 0);
        rotated.dispose();
    }

    private static void drawLegend(Graphics2D g, Iterable<String> names) {
        g.setFont(LABEL_FONT);
        int x = LEFT;
        int index = 0;
        for (String name : names) {
            drawMarker(g, x + MARKER / 2, 60, index);
            g.setColor(SECONDARY_TEXT);
            g.drawString(name, x + MARKER + 8, 65);
            x += MARKER + 8 + g.getFontMetrics().stringWidth(name) + 28;
            index++;
        }
    }

    private static void drawLine(Graphics2D g, TreeMap<Double, Double> points, double maxX, double topY, int index) {
        g.setColor(SERIES_COLORS[index]);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Map.Entry<Double, Double> previous = null;
        for (Map.Entry<Double, Double> point : points.entrySet()) {
            if (previous != null) {
                g.drawLine(toPixelX(previous.getKey(), maxX), toPixelY(previous.getValue(), topY),
                    toPixelX(point.getKey(), maxX), toPixelY(point.getValue(), topY));
            }
            previous = point;
        }
        for (Map.Entry<Double, Double> point : points.entrySet()) {
            drawMarker(g, toPixelX(point.getKey(), maxX), toPixelY(point.getValue(), topY), index);
        }
    }

    private static void drawMarker(Graphics2D g, int x, int y, int index) {
        int half = MARKER / 2;
        int ring = half + 2;
        g.setColor(SURFACE);
        g.fillOval(x - ring, y - ring, ring * 2, ring * 2);
        g.setColor(SERIES_COLORS[index]);
        if (index == 0) {
            g.fillOval(x - half, y - half, MARKER, MARKER);
        } else if (index == 1) {
            g.fillRect(x - half, y - half, MARKER, MARKER);
        } else {
            g.fillPolygon(new int[] {x, x + half + 1, x - half - 1}, new int[] {y - half - 1, y + half, y + half}, 3);
        }
    }

    private static void drawEndLabels(Graphics2D g, List<int[]> labels, List<String> names) {
        labels.sort((first, second) -> second[0] - first[0]);
        g.setFont(LABEL_FONT);
        g.setColor(PRIMARY_TEXT);
        int limit = HEIGHT - BOTTOM + 4;
        for (int[] label : labels) {
            int y = Math.min(label[0] + 5, limit);
            g.drawString(names.get(label[1]), WIDTH - RIGHT + 14, y);
            limit = y - LABEL_GAP;
        }
    }

    private static int toPixelX(double x, double maxX) {
        return LEFT + (int) Math.round(x / maxX * (WIDTH - LEFT - RIGHT));
    }

    private static int toPixelY(double y, double topY) {
        int plotHeight = HEIGHT - TOP - BOTTOM;
        return HEIGHT - BOTTOM - (int) Math.round(y / topY * plotHeight);
    }

    private static double niceStep(double rough) {
        double power = Math.pow(10, Math.floor(Math.log10(rough)));
        double fraction = rough / power;
        if (fraction <= 1) {
            return power;
        }
        if (fraction <= 2) {
            return 2 * power;
        }
        if (fraction <= 5) {
            return 5 * power;
        }
        return 10 * power;
    }

    private static String formatTick(double value, double step) {
        int decimals = Math.max(0, (int) Math.ceil(-Math.log10(step) - 1e-9));
        return String.format(Locale.US, "%." + decimals + "f", value);
    }
}
