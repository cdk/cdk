<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">

<helpset version="2.0">
  <!-- title -->
  <title>JChemPaint User's Guide</title>

  <!-- maps -->
  <maps>
     <homeID>top</homeID>
     <mapref location="jcp.jhm"/>
  </maps>

  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>jcpTOC.xml</data>
  </view>

  <view>
    <name>Favorites</name>
    <label>Favorites</label>
    <type>javax.help.FavoritesView</type>
  </view>
  
  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>

  <presentation default="true" displayviewimages="true">
     <name>main window</name>
     <size width="1024" height="768" />
     <location x="200" y="200" />
     <image>toplevelfolder</image>
     <toolbar>
	<helpaction>javax.help.BackAction</helpaction>
	<helpaction>javax.help.ForwardAction</helpaction>
	<helpaction>javax.help.SeparatorAction</helpaction>
	<helpaction>javax.help.HomeAction</helpaction>
	<helpaction>javax.help.ReloadAction</helpaction>
	<helpaction>javax.help.SeparatorAction</helpaction>
	<helpaction>javax.help.FavoritesAction</helpaction>
	<helpaction>javax.help.SeparatorAction</helpaction>
	<helpaction>javax.help.PrintAction</helpaction>
	<helpaction>javax.help.PrintSetupAction</helpaction>
     </toolbar>
  </presentation>
  
</helpset>

