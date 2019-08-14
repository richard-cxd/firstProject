package pw.wechatbrother.base.utils.jfreechart;


import net.sf.json.JSONObject;
import org.jfree.chart.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import pw.wechatbrother.base.utils.HttpClientUtil;
import pw.wechatbrother.base.utils.JavaUUIDGenerator;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 *
 * 折线图
 *       <p>
 *       创建图表步骤：<br/>
 *       1：创建数据集合<br/>
 *       2：创建Chart：<br/>
 *       3:设置抗锯齿，防止字体显示不清楚<br/>
 *       4:对柱子进行渲染，<br/>
 *       5:对其他部分进行渲染<br/>
 *       6:使用chartPanel接收<br/>
 *
 *       </p>
 */
public class LineChart {
    public LineChart() {
    }

    public DefaultCategoryDataset createDataset() {
        // 标注类别
        String[] categories = { "Jan1", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        Vector<Serie> series = new Vector<Serie>();
        // 柱子名称：柱子所有的值集合
        series.add(new Serie("Tokyo", new Double[] { 49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4 }));
        series.add(new Serie("New York", new Double[] { 83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3 }));
        series.add(new Serie("London", new Double[] { 48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2 }));
        series.add(new Serie("Berlin", new Double[] { 42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1 }));
        /*series.add(new Serie("东京", new Double[] { 49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4 }));
        series.add(new Serie("纽约", new Double[] { 83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3 }));
        series.add(new Serie("伦敦", new Double[] { 48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2 }));
        series.add(new Serie("巴黎", new Double[] { 42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1 }));*/
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils.createDefaultCategoryDataset(series, categories);
        return dataset;
    }

    /**
     *
     * @param categories  x轴枚举
     * @param doubleSeries X轴值
     * @param xTitlEenum  X轴的枚举
     * @return
     */
    public DefaultCategoryDataset createDataset(String[] categories,Double[] doubleSeries,String xTitlEenum) {
        Vector<Serie> series = new Vector<Serie>();
        if(categories==null|| categories.length==0){
            categories = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        }
        if(doubleSeries==null||doubleSeries.length==0){
            doubleSeries=new Double[] { 1.9, 2.5, 3.4, 4.2, 5.0, 6.0, 7.6, 8.5, 9.4, 10.1, 11.6,12.4 };
        }
        series.add(new Serie(xTitlEenum,doubleSeries));
        DefaultCategoryDataset dataset = ChartUtils.createDefaultCategoryDataset(series, categories);
        return dataset;
    }

    public ChartPanel createChart() {
        //创建主题样式
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");
        //设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        // 2：创建Chart[创建不同图形]
        JFreeChart chart = ChartFactory.createLineChart(
                "aaa",//图名字
                "bbb",//横坐标
                "ccc",//纵坐标
                createDataset(),//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                false);// 是否生成超链接
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        ChartUtils.setLineRender(chart.getCategoryPlot(), false,true);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 6:使用chartPanel接收
        ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }

    public static void main(String[] args) {
        /*
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 420);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 创建图形
                ChartPanel chartPanel = new LineChart().createChart();
                frame.getContentPane().add(chartPanel);
                frame.setVisible(true);
            }
        });
        */
       /* LineChart lineChart= new LineChart();
        //创建主题样式
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");
        //设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        // 2：创建Chart[创建不同图形]
        JFreeChart chart = ChartFactory.createLineChart(
                "aaa",//图名字
                "bbb",//横坐标
                "ccc",//纵坐标
                lineChart.createDataset(),//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                false);// 是否生成超链接
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        ChartUtils.setLineRender(chart.getCategoryPlot(), false,true);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        //保存文件
        try {
            saveAsFile(chart,"D:\\UI\\123.png",1024,420);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        LineChart lineChart= new LineChart();

        String[] categories=null;//
        Double[] doubleSeries=null;//
        String xTitlEenum="测试";//
        DefaultCategoryDataset defaultCategoryDataset=lineChart.createDataset(categories,doubleSeries,xTitlEenum);
        try {
            lineChart.sendAsFile(defaultCategoryDataset,"图名字","横坐标","纵坐标");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //发送图片到图片服务器
    public static void sendAsFile(DefaultCategoryDataset categoryDataset,String imgName,String xTitle,String yTitle)throws Exception {
        //创建主题样式
        StandardChartTheme standardChartTheme=new StandardChartTheme("CN");
        //设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书",Font.PLAIN,15));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书",Font.PLAIN,15));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        // 2：创建Chart[创建不同图形]
        JFreeChart chart = ChartFactory.createLineChart(
                imgName,//图名字
                xTitle,//横坐标
                yTitle,//纵坐标
                categoryDataset,//数据集
                //lineChart.createDataset(),//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器
                false);// 是否生成超链接
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        ChartUtils.setLineRender(chart.getCategoryPlot(), false,true);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        String uuid = JavaUUIDGenerator.getUUID();
        String outputPath="D:\\UI\\"+uuid+".png";
        try {
            saveAsFile(chart, outputPath,1024,420);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File myFilePath = new File(outputPath);
        if(myFilePath.exists()) {
            //String url = "http://www.yn5180.com/ynFileServer/uploadFileAndChangeToPDf?directory=/monitoring/chartimg";//这里的值守报表存放的目录,改成值守报告目录【差异】
            String url = "http://www.yn5180.com/ynFileServer/upload?directory=/monitoring/chartimg";//这里的值守报表存放的目录,改成值守报告目录【差异】
            JSONObject jsonObject = HttpClientUtil.uploadFile(myFilePath, url);//上传服务file到文件服务器和转成pdf
            if ((jsonObject != null)) {
                System.out.println(jsonObject.toString());
                myFilePath.delete();
            }
        }
    }

    public static void saveAsFile(JFreeChart chart, String outputPath,
                                  int weight, int height)throws Exception {
        FileOutputStream out = null;
        File outFile = new File(outputPath);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        out = new FileOutputStream(outputPath);
        // 保存为PNG
        ChartUtilities.writeChartAsPNG(out, chart, weight, height);
        // 保存为JPEG
        // ChartUtilities.writeChartAsJPEG(out, chart, weight, height);
        out.flush();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

}