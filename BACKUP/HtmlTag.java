//package com.fajar.util;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
// 
//@Data
//@Builder
//@AllArgsConstructor
//public class HtmlTag {
//	private static Integer Occ;
//
//	@Builder.Default
//	private List<HtmlTag> ValueList = new ArrayList<HtmlTag>();
//
//	private String Key;
//	private Object innerHTML;
//	private String ID;
//	private String className;
//	private String Name;
//	@Builder.Default
//	private List<String> Attributes = new ArrayList<>();
//
//	public void add(HtmlTag Tag) {
//		ValueList.add(Tag);
//
//	}
//
//	public HtmlTag(String Key) {
//		this.Key = Key;
//		Init();
//	}
//
//	public HtmlTag(String Key, Object Value) {
//		this.Key = Key;
//		this.innerHTML = Value;
//		Init();
//	}
//
//	public HtmlTag(String Key, HtmlTag Value) {
//		this.Key = Key;
//		this.innerHTML = Value;
//		Init();
//		ValueList.add(Value);
//	}
//
//	public void Init() {
//		Occ++;
//		Attributes = new ArrayList<String>();
//		className = Key;
//		Name = "custom-component";
//		ID = (new Date().getTime() + Occ)+"";
//	}
//
//	public HtmlTag() {
//		Init();
//	}
//
//	public void add(List<HtmlTag> Tags) {
//		for (HtmlTag Tag : Tags)
//			ValueList.add(Tag);
//
//	}
//
//	public void addAll(HtmlTag... Tag) {
//		for (int i = 0; i < Tag.length; i++) {
//			ValueList.add(Tag[i]);
//		}
//
//	}
//
//	public void clear() {
//		ValueList.clear();
//	}
//
//	public int ListValueCount() {
//		return ValueList.size();
//	}
//
//	public List<HtmlTag> GetListValue() {
//		return ValueList;
//	}
//
//	public void addAttribute(String Key, String Value) {
//		RemoveIfExist(Key);
//		Attributes.add(Key + "=\"" + Value + "\"");
//	}
//
//	public void addAttribute(String... KeynVal) {
//		if (KeynVal.length % 2 != 0) {
//			return;
//		}
//		String CurrentKey = "";
//		String CurrentVal = "";
//		for (int i = 0; i < KeynVal.length; i++) {
//
//			if ((i + 1) % 2 != 0 || (i + 1) == 1) // odd value
//			{
//				CurrentKey = KeynVal[i];
//			} else // even value
//			{
//				CurrentVal = KeynVal[i];
//				RemoveIfExist(CurrentKey);
//				addAttribute(CurrentKey, CurrentVal);
//			}
//		}
//	}
//
//	private void RemoveIfExist(String Key) {
//		for (String Attr : Attributes)
//			if (Attr.toLowerCase().startsWith(Key.toLowerCase() + "=")) {
//				Attributes.remove(Attr);
//				break;
//			}
//
//	}
//
//	public boolean HasAttribute(String Key) {
//		for (String Attr : Attributes)
//			if (Attr.toLowerCase().startsWith(Key.toLowerCase() + "="))
//				return true;
//		return false;
//	}
//}
