[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![](https://jitpack.io/v/HamidrezaAmz/MagicalPdfEditor.svg)](https://jitpack.io/#HamidrezaAmz/MagicalPdfEditor)

# MagicalPdfEditor
This is a small PDF editor based on [OpenPDF Core](https://github.com/HamidrezaAmz/OpenPDF) and [AndroidPdfViewer](https://github.com/HamidrezaAmz/MagicalPdfViewer).
As there is not much open source easy working PDF editors and PDF wizards, I decied to create a simple directory to resolve my issues. Here I have worked on two seprate cores, add some functionality into them and combining them together to acheive my target. I am working on this repo, any help will be appreciated.

# Features
* [X] All functinality in [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer) (Load, Read, Render, Show, ... PDF Files)
* [x] Add annotations into pdf file
* [x] Add Image into pdf file as OCG item
* [x] Remove annotations from pdf file
* [x] Update annotation in pdf file
* [x] Save changes on same pdf file
* [x] Add observer pattern for better UX (prevent freez)
* [ ] Draw line stream over pdf 
* [ ] Highlight text in pdf
* [ ] Remove page from pdf file
* [x] Convert image to pdf with scale and zoom support
* [ ] Maybe some other features :stuck_out_tongue_winking_eye:

# Usage
Just clone the project and trance the source code, It's realy easy and clear. Here an example how to use this lib.

**Step 1.** Add the JitPack repository to your build file,
Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
```

**Step 2.** Add the dependency

```gradle
dependencies {
    implementation 'com.github.HamidrezaAmz:MagicalPdfEditor:LAST_VERSION'
}
```

**Step 3.** Add PDFViewer into your xml
```xml
<ir.vasl.magicalpec.view.MagicalPdfViewer
            android:id="@+id/magicalPdfViewer"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#009688" />
```

For more examples and sample code please check [this class](https://github.com/HamidrezaAmz/MagicalPdfEditor/blob/master/app/src/main/java/ir/vasl/magicalpdfeditor/MainActivity.java) from sample app.

# Interfaces
Here is the list of interfaces that can help you work with this lib.

| Interface | Description |
| --- | --- |
| OnPageErrorListener | Triggered when sth went wrong on loading the pdf file |
| OnLoadCompleteListener | Triggered when loading pdf file finished |
| OnPageChangeListener | Triggered when page status changed, this will give you page count an new page index |
| OnLongPressListener | Triggered when user hold his/her finger on part of pdf screen, this will give you `MotionEvent` object |
| OnTapListener | Triggered when user on tap on PDF screen, this will give you `MotionEvent` object | 
| LinkHandler | Triggered when user clicked on added annotation with link, this will give you `LinkTapEvent` you can extract stored data |


# Attention
Working with files is **restricted** in higher android api levels, So if you are working with high level android APIs, be aware of using file provides and needed permissions to work with files, Or you can use [SimpleStorage](https://github.com/anggrayudi/SimpleStorage) lib to work with files and Android storage. 

