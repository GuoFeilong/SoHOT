package com.sohot.hot.jsoup;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sohot.hot.MyConstant;
import com.sohot.hot.model.Category;
import com.sohot.hot.model.CategoryBookItemDesc;
import com.sohot.hot.model.CategoryForJson;
import com.sohot.hot.model.CategoryItem;
import com.sohot.hot.model.CategoryItemTableBookItem;
import com.sohot.hot.model.CategoryItemTableFilmItem;
import com.sohot.hot.model.CategoryItemTableItem;
import com.sohot.hot.model.CategoryPicItemDesc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jsion on 15/11/19.
 */
public class GetHotData {
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private CategoryForJson forJson;
    private ArrayList<Category> categories;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * 加载完毕数据的监听
     */
    public interface HasGetHotMenuDataListener {
        void hasGetMenuData(ArrayList<Category> data);
    }

    private HasGetHotMenuDataListener hasGetHotMenuDataListener;

    public void setHasGetHotMenuDataListener(HasGetHotMenuDataListener hasGetHotMenuDataListener) {
        this.hasGetHotMenuDataListener = hasGetHotMenuDataListener;
    }

    /**
     * 获取到图片列表数据的监听
     */
    public interface HasGetHotTableDataListener {
        void hasGetHotTableData(ArrayList<CategoryItemTableItem> tableItems);
    }

    private HasGetHotTableDataListener hasGetHotTableDataListener;

    public void setHasGetHotTableDataListener(HasGetHotTableDataListener hasGetHotTableDataListener) {
        this.hasGetHotTableDataListener = hasGetHotTableDataListener;
    }


    /**
     * 获取图片列表对应所有图片链接的监听
     */
    public interface HasGetHotPicItemHrefsListener {
        void hasGetHotTableData(ArrayList<CategoryPicItemDesc> tableItems);
    }

    private HasGetHotPicItemHrefsListener hasGetHotPicItemHrefsListener;

    public void setHasGetHotPicItemHrefsListener(HasGetHotPicItemHrefsListener hasGetHotPicItemHrefsListener) {
        this.hasGetHotPicItemHrefsListener = hasGetHotPicItemHrefsListener;
    }

    /**
     * 获取电影列表的监听
     */
    public interface HasGetHotTableFilmDataListener {
        void hasGetHotTableFilmData(ArrayList<CategoryItemTableFilmItem> filmItems);
    }

    private HasGetHotTableFilmDataListener hasGetHotTableFilmDataListener;

    public void setHasGetHotTableFilmDataListener(HasGetHotTableFilmDataListener hasGetHotTableFilmDataListener) {
        this.hasGetHotTableFilmDataListener = hasGetHotTableFilmDataListener;
    }


    /**
     * 获取book列表的监听
     */
    public interface HasGetHotTableBookDataListener {
        void hasGetHotTableBookData(ArrayList<CategoryItemTableBookItem> filmItems);
    }

    private HasGetHotTableBookDataListener hasGetHotTableBookDataListener;

    public void setHasGetHotTableBookDataListener(HasGetHotTableBookDataListener hasGetHotTableBookDataListener) {
        this.hasGetHotTableBookDataListener = hasGetHotTableBookDataListener;
    }

    /**
     * 获取书详情
     */
    public interface HasGetHotTableBookDetailListener {
        void hasGetHotTableBookDetails(ArrayList<CategoryBookItemDesc> bookDetail);
    }

    private HasGetHotTableBookDetailListener hasGetHotTableBookDetailListener;

    public void setHasGetHotTableBookDetailListener(HasGetHotTableBookDetailListener hasGetHotTableBookDetailListener) {
        this.hasGetHotTableBookDetailListener = hasGetHotTableBookDetailListener;
    }


    /**
     * 获取分类信息的数据
     */
    public void getCategoryMenuData() {
        categories = new ArrayList<>();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
                    document = Jsoup.connect(MyConstant.HOT_ADDRESS_HOME).userAgent(MyConstant.USER_AGENT).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element body = document.body();
//                    Log.i("menu菜单======", "菜单数据: >>>>>>>>>>>"+body.toString());
                    Elements elements = body.select("div#menu_box > div#ks > #menu");
//                    Log.i("menu菜单======", "菜单数据: >>>>>>>>>>>"+elements.toString());
                    for (int i = 0; i < elements.size(); i++) {
                        Element ul = elements.get(i);
                        Elements uls = ul.select("li");


                        ArrayList<CategoryItem> tempCI = new ArrayList<CategoryItem>();
                        Category tempC = new Category();

                        for (int j = 0; j < uls.size(); j++) {
                            Element element = uls.get(j);
                            String categoryItemTitle = element.text();
                            String categoryItemHref = MyConstant.HOT_ADDRESS_HOME + element.select("a").attr("href");

                            if (j == 0) {
                                tempC.setCategoryTitle(categoryItemTitle);
                            } else {
                                CategoryItem categoryItem = new CategoryItem();
                                categoryItem.setCategoryItemTitle(categoryItemTitle);
                                categoryItem.setCategoryItemHref(categoryItemHref);
                                tempCI.add(categoryItem);
                            }

                        }
                        tempC.setCategoryItems(tempCI);
                        categories.add(tempC);
                    }
//                    Log.i("======集合数据===", categories.size() + ">>>>>>>>>>>>" + categories.toString());

                    if (hasGetHotMenuDataListener != null) {
                        hasGetHotMenuDataListener.hasGetMenuData(categories);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * 根据当前的条目是获取表格中的pic数据
     *
     * @param item
     */
    public void getCategoryTablePicItem(final CategoryItem item) {
        final ArrayList<CategoryItemTableItem> tableItems = new ArrayList<>();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
                    Log.i("超链接====", "run: >>>>>>>>>>>" + item.getCategoryItemHref());
                    document = Jsoup.connect(item.getCategoryItemHref()).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element body = document.body();
                    Elements elements = body.select("div#wrap > div#ks > div#ks_xp >div.main > div.list > table.listt");

                    Elements elementForAllPageIndex = body.select("div#wrap > div#ks > div#ks_xp >div.box_page>div.page>span");
                    String temp = elementForAllPageIndex.text();
                    String[] temps = temp.split("/");
                    String tempS = temps[1].toString();
                    String[] temps2 = tempS.split(" ");
                    String tempSS = temps2[0].toString();
                    String maxPage = tempSS.toString();
                    String currentMaxPage = maxPage.substring(0, maxPage.length() - 1);
                    int maxP = Integer.parseInt(currentMaxPage);
                    ArrayList<String> allHrefs = new ArrayList<String>();
                    for (int j = 2; j < maxP + 1; j++) {
                        allHrefs.add(item.getCategoryItemHref() + "index" + j + ".html");
                    }
                    Log.e("所有图片的超链>>>>", "最后一页的超链接: >>>>" + allHrefs.get(allHrefs.size() - 1) + "<<<<<<<<");

                    for (int i = 0; i < elements.size(); i++) {
                        CategoryItemTableItem tableItem = new CategoryItemTableItem();
                        Element ul = elements.get(i);
                        String itemName = ul.text();
                        String itemPicHref = MyConstant.HOT_ADDRESS_HOME + ul.select("a").attr("href");
                        String updateDate = ul.select("tr > .date").text();
                        tableItem.setCategoryItemTitle(itemName);
                        tableItem.setCategoryItemHref(itemPicHref);
                        tableItem.setUpdateDate(updateDate);
                        tableItem.setCategoryAllPageIndex(allHrefs);
                        tableItems.add(tableItem);
//                        Log.i("子分类标题", "run: >>>>>" + ul.toString());
                    }
                    if (hasGetHotTableDataListener != null) {
                        hasGetHotTableDataListener.hasGetHotTableData(tableItems);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * 获取对应pic标题所有的图片
     *
     * @param item
     */
    public void getPicItemHrefs(final CategoryItemTableItem item) {
        final ArrayList<CategoryPicItemDesc> allPics = new ArrayList<>();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
//                    Log.i("超链接====", "run: >>>>>>>>>>>" + item.getCategoryItemHref());
                    document = Jsoup.connect(item.getCategoryItemHref()).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element body = document.body();
                    Elements elements = body.select("div#wrap > div#ks > div#ks_xp >div.main > div.content > div > div.n_bd ");
                    Elements els = null;
                    for (int i = 0; i < elements.size(); i++) {
                        Element ul = elements.get(i);
                        els = ul.select("img");
//                            Log.i("所有图片的网络地址=======", "run: >>>>>" + ul.toString());

                        for (int j = 1; j < els.size(); j++) {
                            //第一条是广告图片,所以不加载
                            CategoryPicItemDesc picItemDesc = new CategoryPicItemDesc();
                            String picUrl = els.get(j).attr("src");
                            picItemDesc.setItemPicHrefs(picUrl);
                            picItemDesc.setItemTitle(item.getCategoryItemTitle());
                            allPics.add(picItemDesc);
//                            Log.i("所有图片的网络地址=======", "run: >>>>>" + picUrl);
                        }
                    }


                    if (hasGetHotPicItemHrefsListener != null) {
                        hasGetHotPicItemHrefsListener.hasGetHotTableData(allPics);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * 根据当前的条目是获取表格中的大片数据
     *
     * @param item
     */
    public void getCategoryTableFilmItem(final CategoryItem item) {
        final ArrayList<CategoryItemTableFilmItem> tableItems = new ArrayList<>();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
//                    Log.i("电影条目超链", "run: >>>>>>>>>>>" + item.getCategoryItemHref());
                    document = Jsoup.connect(item.getCategoryItemHref()).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element body = document.body();
                    Elements elements = body.select("div#wrap > div#w980 > div#w700 > div.box.pic_list > div.text > ul > li");


                    Elements elementForAllPageIndex = body.select("div#wrap > div#w980 > div#w700 >div.box.pic_list >div.box_page > div.page > span");
                    String temp = elementForAllPageIndex.text();
                    String[] temps = temp.split("/");
                    String tempS = temps[1].toString();
                    String[] temps2 = tempS.split(" ");
                    String tempSS = temps2[0].toString();
                    String maxPage = tempSS.toString();
                    String currentMaxPage = maxPage.substring(0, maxPage.length() - 1);
                    int maxP = Integer.parseInt(currentMaxPage);
                    ArrayList<String> allHrefs = new ArrayList<String>();
                    for (int j = 2; j < maxP + 1; j++) {
                        allHrefs.add(item.getCategoryItemHref() + "index" + j + ".html");
                    }
                    Log.e("所有Film的超链>>>>", "最后一页的超链接: >>>>" + allHrefs.get(allHrefs.size() - 1) + "<<<<<<<<");


                    for (int i = 0; i < elements.size(); i++) {
                        CategoryItemTableFilmItem tableItem = new CategoryItemTableFilmItem();
                        Element ul = elements.get(i);
                        String itemHref = ul.select(".pic").select("a").attr("href");
                        String itemFilmName = ul.select(".txt > p.s").text();
                        String itemFilmIconNetAddress = MyConstant.HOT_ADDRESS_HOME + ul.select("div.pic").select("a").select("img").attr("src");

                        tableItem.setItemHref(MyConstant.HOT_ADDRESS_HOME + itemHref);
                        tableItem.setItemCategoryName(item.getCategoryItemTitle());
                        tableItem.setItemFilmName(itemFilmName);
                        tableItem.setItemFilmIconNetAddress(itemFilmIconNetAddress);
                        tableItem.setAllFilmPageIndex(allHrefs);
                        tableItems.add(tableItem);
//                        Log.i("子分类标题", "run: >>>>>" + ul.toString());
//                        Log.i("子分类标题....txt", "run: >>>>>" + tableItem.toString());
                    }
                    if (hasGetHotTableFilmDataListener != null) {
                        hasGetHotTableFilmDataListener.hasGetHotTableFilmData(tableItems);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * 根据当前的条目是获取表格中BOOK的数据
     *
     * @param item
     */
    public void getCategoryTableBookItem(final CategoryItem item) {
        final ArrayList<CategoryItemTableBookItem> tableItems = new ArrayList<>();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
//                    Log.i("小说条目超链", "run: >>>>>>>>>>>" + item.getCategoryItemHref());
                    document = Jsoup.connect(item.getCategoryItemHref()).timeout(MyConstant.HOT_TIME_OUT).get();
                    Log.i("小说的document", "run: >>>>>>>>>>>" + document.toString());

                    Element body = document.body();
                    Elements elements = body.select("div#wrap > div#ks > div#ks_xp > div.main > div.list > table.listt");
//                    Log.i("小说的数据", "小说: >>>>>>>>>>>" + elements.toString());


                    Elements elementForAllPageIndex = body.select("div#wrap > div#ks > div#ks_xp >div.box_page>div.page>span");
                    String temp = elementForAllPageIndex.text();
                    String[] temps = temp.split("/");
                    String tempS = temps[1].toString();
                    String[] temps2 = tempS.split(" ");
                    String tempSS = temps2[0].toString();
                    String maxPage = tempSS.toString();
                    String currentMaxPage = maxPage.substring(0, maxPage.length() - 1);
                    int maxP = Integer.parseInt(currentMaxPage);
                    ArrayList<String> allHrefs = new ArrayList<String>();
                    for (int j = 2; j < maxP + 1; j++) {
                        allHrefs.add(item.getCategoryItemHref() + "index" + j + ".html");
                    }
                    Log.e("所有BOOK的超链>>>>", "最后一页的超链接: >>>>" + allHrefs.get(allHrefs.size() - 1) + "<<<<<<<<");


                    for (int i = 0; i < elements.size(); i++) {
                        Element ul = elements.get(i);
                        CategoryItemTableBookItem bookItem = new CategoryItemTableBookItem();
                        String bookName = ul.select(".listtitletxt").text();
                        String bookHref = ul.select(".listtitletxt").select("a").attr("href");
                        String bookUpdateTime = ul.select(".date").text();

                        bookItem.setItemBookName(bookName);
                        bookItem.setItemBookHref(MyConstant.HOT_ADDRESS_HOME + bookHref);
                        bookItem.setItemBookUpdateTime(bookUpdateTime);

                        bookItem.setAllBookPageIndex(allHrefs);
                        tableItems.add(bookItem);
//                        Log.i("小说---子分类标题", "run: >>>>>" + ul.toString());
                    }
                    if (hasGetHotTableBookDataListener != null) {
                        hasGetHotTableBookDataListener.hasGetHotTableBookData(tableItems);
                    }
//                    Log.i("小说---子分类标题", "run: >>>>>" + tableItems.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    /**
     * 根据当前的条目是获取表格中BOOK的数据
     *
     * @param item
     */
    public void getCategoryTableBookItemDesc(final CategoryItemTableBookItem item) {
        final ArrayList<CategoryBookItemDesc> bookDetails = new ArrayList<>();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
//                    Log.i("小说条目超链", "run: >>>>>>>>>>>" + item.getItemBookHref());
                    document = Jsoup.connect(item.getItemBookHref()).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element body = document.body();
                    Elements elements = body.select("div#wrap > div#ks > div#ks_xp >div.main > div.content > div > div.n_bd ");
                    for (int i = 0; i < elements.size(); i++) {
                        CategoryBookItemDesc itemDesc = new CategoryBookItemDesc();
                        Element ul = elements.get(i);
                        String bookContent = ul.toString().replace("<br>", "\n");
                        String regEx_html = "<[^>]+>";
                        itemDesc.setBookContent(bookContent.replaceAll(regEx_html, ""));
                        itemDesc.setBookName(item.getItemBookName());
                        bookDetails.add(itemDesc);
                        Log.i("小说---详情", "run: >>>>>" + ul.toString());
                    }
                    if (hasGetHotTableBookDetailListener != null) {
                        hasGetHotTableBookDetailListener.hasGetHotTableBookDetails(bookDetails);
                    }
//                    Log.i("小说---子分类标题", "run: >>>>>" + tableItems.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface HasGetNextHref {
        void hasGetNextHref(String nextHref);
    }

    private HasGetNextHref hasGetNextHref;

    public void setHasGetNextHref(HasGetNextHref hasGetNextHref) {
        this.hasGetNextHref = hasGetNextHref;
    }

    /**
     * 获取当前film的下一级界面的超链
     */
    public void getCurrentFilmNextHref(final String currentHref) {
        final ArrayList<CategoryBookItemDesc> bookDetails = new ArrayList<>();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Document document;
                try {
                    document = Jsoup.connect(currentHref).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element body = document.body();
                    Elements elements = body.select("div.ContentBox > li");
                    final String nextHref = MyConstant.HOT_ADDRESS_HOME + elements.select("a").attr("href").toString();
                    Log.e("TAG____电影超链next", "run: >>>>>" + "----" + nextHref);


                    document = Jsoup.connect(nextHref).timeout(MyConstant.HOT_TIME_OUT).get();
                    Element nextBody = document.body();
                    Log.e("TAG___next", "run: >>>>下一个body" + nextBody.html());

                    if (hasGetNextHref!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                hasGetNextHref.hasGetNextHref(nextHref);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }


}
