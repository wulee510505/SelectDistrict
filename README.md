# SelectDistrict
省市区选择库 用法
效果图如下：
![](https://raw.githubusercontent.com/wulee510505/SelectDistrict/master/screenshots/1.jpg)
![](https://raw.githubusercontent.com/wulee510505/SelectDistrict/master/screenshots/2.jpg)
![](https://raw.githubusercontent.com/wulee510505/SelectDistrict/master/screenshots/3.jpg)

Step 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.wulee510505:SelectDistrict:1.0.3'
	}
	
activity调用方法如下：

   Intent intent = new Intent(MainActivity.this, FindPlaceActivity.class);
   intent.putExtra(FindPlaceActivity.INTENT_REQ_LEVEL,3);//选择级别，默认是省市区三级
   intent.putExtra(FindPlaceActivity.INTENT_BG_TITLE_COLOR,ContextCompat.getColor(MainActivity.this,R.color.color_orange_dark));//标题色    intent.putExtra(FindPlaceActivity.INTENT_BG_GRIDITEM_DEF_COLOR, Color.GREEN);//item默认颜色
   intent.putExtra(FindPlaceActivity.INTENT_BG_GRIDITEM_SEL_COLOR, Color.RED);//item选中颜色
   startActivityForResult(intent,PLACE_REQUEST);
	
fragment调用方法：

   FragmentManager fragmentManager = getSupportFragmentManager();
   FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

   final FindPlaceFragment findPlaceFragment = new FindPlaceFragment();
   Bundle bundle = new Bundle();
   bundle.putInt(FindPlaceFragment.INTENT_REQ_LEVEL, 3);//选择级别，默认是省市区三级
   bundle.putInt(FindPlaceActivity.INTENT_BG_GRIDITEM_DEF_COLOR, Color.YELLOW);//item默认颜色
   bundle.putInt(FindPlaceActivity.INTENT_BG_GRIDITEM_SEL_COLOR, Color.GREEN);//item选中颜色
   findPlaceFragment.setArguments(bundle);

   fragmentTransaction.add(R.id.fragment_container, findPlaceFragment);
   fragmentTransaction.commit();
		
