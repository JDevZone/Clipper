# Clipper
Custom Image Clipper

[![](https://jitpack.io/v/JDevZone/Clipper.svg)](https://jitpack.io/#JDevZone/Clipper)

---------------------------
### Installation

1. Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


2. Add the dependency in app gradle

```groovy
	dependencies {
	        implementation 'com.github.JDevZone:Clipper:{latest_version}'
	}
```
### Basic Usage

```xml
<com.jdevzone.clipper.ClipImage
            android:layout_width="match_parent"
            android:layout_height="150dp"
            app:ci_stroke_color="#f00"
            app:ci_stroke_type="dashed"
            app:ci_stroke_width="4dp"
            app:ci_stroke_path_interval="5dp"
            app:ci_stroke_path_phase="0dp"
            app:ci_clip_radius="35dp"
            app:ci_clip_type="pin"
            app:ci_clip_gravity="bottom"
            app:ci_corner_radius="10dp"
            app:ci_horizontal_padding="20dp"
            app:ci_vertical_padding="10dp"
            app:srcCompat="@drawable/gradient_shape" />
```

### Sample
<div align="left">
  <img src="https://github.com/JDevZone/Clipper/blob/main/graphics/sample_1.png" alt="" width="180px">
  <img src="https://github.com/JDevZone/Clipper/blob/main/graphics/sample_2.png" alt="" width="180px">
</div>

-----------------
