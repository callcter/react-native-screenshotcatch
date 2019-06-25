
# react-native-screenshotcatch

## Getting started

`$ npm install react-native-screenshotcatch --save`

### Mostly automatic installation

`$ react-native link react-native-screenshotcatch`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-screenshotcatch` and add `RNScreenshotcatch.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNScreenshotcatch.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

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