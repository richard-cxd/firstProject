package pw.wechatbrother.base.utils.jfreechart;


import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**折线图后台生成的另一种方式(待用，可用)
 * Created by zhengjingli on 2017/7/18.
 */
public class Line {
    public static void main(String[] args) {
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
        ChartFactory.setChartTheme(mChartTheme);
        CategoryDataset mDataset = GetDataset();
        JFreeChart mChart = ChartFactory.createLineChart(
                "折线图",//图名字
                "年份",//横坐标
                "数量",//纵坐标
                mDataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器 
                false);// 是否生成超链接

//        CategoryPlot mPlot = (CategoryPlot)mChart.getPlot();
//        mPlot.setBackgroundPaint(Color.LIGHT_GRAY);
//        mPlot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
//        mPlot.setOutlinePaint(Color.RED);//边界线
        // 设置总的背景颜色
        mChart.setBackgroundPaint(ChartColor.WHITE);//
// 设置标题颜色
        mChart.getTitle().setPaint(ChartColor.blue);
// 获得图表对象
        CategoryPlot p = mChart.getCategoryPlot();
// 设置图的背景颜色
        p.setBackgroundPaint(ChartColor.WHITE);
// 设置表格线颜色
        p.setRangeGridlinePaint(ChartColor.red);

        ChartFrame mChartFrame = new ChartFrame("折线图", mChart);
        mChartFrame.pack();
        mChartFrame.setVisible(true);

    }
    public static CategoryDataset GetDataset()
    {
        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        for(int i=0;i<10;i++){
            int max=100;
            int min=1;
            Random random = new Random();
            int s = random.nextInt(max)%(max-min+1) + min;
            mDataset.addValue(s, "First", i+"");
        }
        for(int i=0;i<7;i++){
            int max=100;
            int min=1;
            Random random = new Random();
            int s = random.nextInt(max)%(max-min+1) + min;
            mDataset.addValue(s, "Second", i+"");
        }
        System.out.println("加载总数据"+mDataset.getColumnCount());
        //addValue(double value, Comparable rowKey, Comparable columnKey)
        /*mDataset.addValue(1, "First", "2013");
        mDataset.addValue(3, "First", "2014");
        mDataset.addValue(2, "First", "2015");
        mDataset.addValue(6, "First", "2016");
        mDataset.addValue(5, "First", "2017");
        mDataset.addValue(12, "First", "2018");
        mDataset.addValue(14, "Second", "2013");
        mDataset.addValue(13, "Second", "2014");
        mDataset.addValue(12, "Second", "2015");
        mDataset.addValue(9, "Second", "2016");
        mDataset.addValue(5, "Second", "2017");
        mDataset.addValue(7, "Second", "2018");*/
        return mDataset;
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

    public static Map getDataToFileAndSaveTOThisTomcat(CategoryDataset mDataset, String outputPath,Integer weight, Integer height){
        Map returnMap = new HashMap();
        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
        ChartFactory.setChartTheme(mChartTheme);
        //CategoryDataset mDataset = GetDataset();
        if(weight==null||weight.equals("")||weight==0){
            weight=1024;
        }
        if(height==null||height.equals("")||height==0){
           height=420;
        }
        String hengzuobiao="";

        JFreeChart mChart = ChartFactory.createLineChart(
                "",//图名字
                hengzuobiao,//横坐标 例如：年份
                "",//纵坐标
                mDataset,//数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 采用标准生成器 
                false);// 是否生成超链接
        // 设置总的背景颜色
        mChart.setBackgroundPaint(ChartColor.WHITE);
// 设置标题颜色
        mChart.getTitle().setPaint(ChartColor.blue);
// 获得图表对象
        CategoryPlot p = mChart.getCategoryPlot();
// 设置图的背景颜色
        p.setBackgroundPaint(ChartColor.WHITE);
// 设置表格线颜色
        p.setRangeGridlinePaint(ChartColor.red);
        try {
            saveAsFile(mChart,outputPath,weight,height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;

    }


}