
# react-native-screenshotcatch

## Warning

- Apple audit specification: Screen capture not allowed to block content
- It's not compatible to some Android 10 devices

## Getting started

`$ npm install react-native-screenshotcatch --save`

### Installation

#### iOS

RN version >= 0.60

autolink

RN Version < 0.60
```
pod 'RNScreenshotcatch', :path => '../node_modules/react-native-screenshotcatch'
```

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.dreamser.screenshotcatch.RNScreenshotcatchPackage;` to the imports at the top of the file
  - Add `new RNScreenshotcatchPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-screenshotcatch'
  	project(':react-native-screenshotcatch').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-screenshotcatch/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-screenshotcatch')
  	```

## Config

#### Android

```
// MainActivity.java
import com.dreamser.screenshotcatch.RNScreenshotcatchModule;
public class MainActivity extends ReactActivity {
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenShotShareModule.initScreenShotShareSDK(this);  // here
    }
    // ...other code
}
```

add

## Usage
```javascript
import RNScreenshotcatchUtil from 'react-native-screenshotcatch';

export default class Root extends React.Component{
	componentWillMount(){
		ScreenShotShareUtil.startListener(res => {
      if(res && res.code === 200){
        // success
      }else{
				// fail
      }
    })
	}
	componentWillUnmount(){
    ScreenShotShareUtil.stopListener()
  }
}

```

## Other API

Judge Android phone has NavigationBar
```
const hasBar = await ScreenShotShareUtil.hasNavigationBar()
```