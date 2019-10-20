//package com.fajar.util;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HtmlUtil {
//
//    public static HtmlTag GenerateTable(int Col, HtmlTag[] Elements)
//    {
//        HtmlTag Table = new HtmlTag("table");
//        int ColIdx = 1;
//        HtmlTag CurrentTr = new HtmlTag("tr");
//        List<HtmlTag> Tds = new ArrayList<HtmlTag>();
//        for (int i = 0; i < Elements.length; i++)
//        {
//            HtmlTag Tag = Elements[i];
//
//            HtmlTag Td = new HtmlTag("td", Tag);
//            Tds.add(Td);
//            if (ColIdx == Col || i == Elements.length - 1)
//            {
//                ColIdx = 0;
//                CurrentTr.add(Tds);
//                Table.add(CurrentTr);
//                Tds.clear();
//                CurrentTr = new HtmlTag("tr");
//            }
//            ColIdx++;
//        }
//
//        return (Table);
//    }
//
//    
//
//    public static HtmlTag DivLabel(String Text)
//    {
//        return new HtmlTag("div", Text);
//    }
//
//    public static HtmlTag Wrap(String Class,  HtmlTag ...Tags)
//    {
//        HtmlTag Wrapper = new HtmlTag("div");
//        if (Class != null)
//            Wrapper.setClassName(Class);
//        for (HtmlTag Tag : Tags)
//        {
//            Wrapper.add(Tag);
//        }
//        return Wrapper;
//    }
//
//}
