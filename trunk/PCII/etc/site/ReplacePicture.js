
var oldPicture = "";
var newPictures = new Array(
"tiger-turret.jpg",
"tiger-fires-on-sherman-on-the-bridge.jpg",
"mig-flies-over-battlefield.jpg");

function mouseOver() 
{
 var index=arguments[1];
 oldPicture=arguments[0].src;
 arguments[0].src=newPictures[index];
}
function mouseOut()
{
 var index=arguments[1];
 arguments[0].src=oldPicture;
}