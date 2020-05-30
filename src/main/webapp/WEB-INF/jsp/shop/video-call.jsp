<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%><!DOCTYPE html>
<div class="content" onload="initLiveStream()">
	<h2>Video Call With</h2>
	<h3>Partner ID: ${partnerId }</h3>
	<a href="<spring:url value="/admin/home" />">Back</a>

	<h2>Live Streaming</h2>
	<p>Stream ID: ${registeredRequestId}</p>
	<canvas id="canvas"> </canvas>

	<div className="camera">
		<h2>You</h2>
		<video controls id="video">Video stream not available.
		</video>
		<div>
			<button id="startbutton" onClick="takepicture()">Take photo</button>
			<button onClick="clearphoto()">Clear Photo</button>
			<button onClick="terminate()">Terminate</button>
		</div>

	</div>
	<div className="output-receiver x"
		style="width: 500px; height: 450px; border: solid 1px green">
		<h2>Partner</h2>
		<img width="300" height="300" id="photo-receiver"
			alt="The screen RECEIVER will appear in this box." />
	</div>

	<hr />
</div>
<script type="text/javascript">

var video;
var canvas;
var photoReceiver;
var terminated = false;
const receiver = "${partnerId}";
var latestImageResponse = {};

function init () {
    const _class = this;
    window.navigator.mediaDevices.getUserMedia({ video: true, audio: false })
        .then(function (stream) {
            _class.video.srcObject = stream;
           // console.log("stream:", stream); 
            _class.video.play();
           
        })
        .catch(function (err) {
            console.log("An error occurred: " + err);
        });
   
    this.video.addEventListener('canplay', function (ev) {
        if (!_class.streaming) {
            _class.height = _class.video.videoHeight / (_class.video.videoWidth / _class.width);

            _class.video.setAttribute('width', _class.width);
            _class.video.setAttribute('height', _class.height);
            _class.canvas.setAttribute('width', _class.width);
            _class.canvas.setAttribute('height', _class.height);
            _class.streaming = true; 
           
        }
    }, false);  

    this.clearphoto();
} 

function terminate (){
    this.terminated = true;
}

function setSendingVideoFalse () {
   this.sendingVideo = false;
}

function sendVideoImage (imageData)  {

    if(this.sendingVideo == true || this.terminated){
        return;
    }
    this.sendingVideo = true;

    const requestId = "${registeredRequestId}";
    const receiver =  this.props.receiver;
    this.sendVideoImage(imageData, requestId, receiver);  
    
}

function sendVideoImage(imageData, requestId, partnedId){
	/* console.log("--------------SEND VIDE IMAGE--------------"); 
	console.log("Origin ReqID: ", requestId);
	console.log("Receiver: ", receiver);
	console.log("Image Data: ",imageData); */
	sendToWebsocket("/app/stream", {
		partnedId : partnedId,
		originId : requestId,
		imageData : imageData
	});
}

function handleLiveStream(response)  { 
    if(this.terminated){
        return;
    }
    this.latestImageResponse = response;
    const _class = this;
    this.populateCanvas().then((base64)=>{
        _class.photoReceiver.setAttribute('src', base64 );
    });
}

function populateCanvas()  {
    const _class = this;
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.src =  _class.latestImageResponse.imageData;
        
        img.onload = function () {
            var newDataUri = _class.imageToDataUri(this, 300, 300);
            resolve(newDataUri);
        }; 
 });
}

 function takepicture () {
    const _class = this;
    this.resizeWebcamImage().then((data)=>{
        _class.sendVideoImage(data);
    })

    // var context = this.canvas.getContext('2d');
    // if (this.width && this.height) {
    //     this.canvas.width = this.width/ 5;
    //     this.canvas.height = this.height/ 5;
    //     context.drawImage(this.video, 0, 0, this.width/ 5, this.height/ 5);

         

      
    // } else {
    //     this.clearphoto();
    // }
}

function resizeWebcamImage () {
    const _class = this;
    return new Promise((resolve, reject)=>{
        var context = _class.canvas.getContext('2d');
        resolve(_class.canvas.toDataURL('image/png'));
        context.drawImage(_class.video, 0, 0, _class.width , _class.height );
         
        // if (_class.width && _class.height) {
        //     const dividier = 1;
        //     _class.canvas.width = _class.width/ dividier;
        //     _class.canvas.height = _class.height/ dividier;
        //     context.drawImage(_class.video, 0, 0, _class.width/ dividier, _class.height/dividier);
        //     var data = _class.canvas.toDataURL('image/png');  
        //     resolve(data);
        // }else {
        //     _class.clearphoto();
        // }
    })

   
}

function imageToDataUri (img, width, height)   {

    var ctx = theCanvas.getContext('2d');

    // set its dimension to target size
    theCanvas.width = width;
    theCanvas.height = height;

    // draw source image into the off-screen canvas:
    ctx.drawImage(img, 0, 0, width, height);

    // encode image to data-uri with base64 version of compressed image
    return theCanvas.toDataURL('image/png');
}

function clearphoto () {
    // var context = this.canvas.getContext('2d');
    // context.fillStyle = "#AAA";
    // context.fillRect(0, 0, this.canvas.width, this.canvas.height);

    // var data = this.canvas.toDataURL('image/png'); 
    // var img = new Image();
    // img.src = data;
}


/**
* ==================================================
*                  Frame Loop
* ================================================== 
*/

function initAnimation () {
    this.isAnimate = !this.isAnimate;
    const _class = this;
    window.requestAnimationFrame(function () {
        _class.animate()
    });
}
function animate(){
    
    this.clearphoto();
    this.takepicture(); 
    const _class = this;
    if (this.isAnimate) {
        window.requestAnimationFrame(function () {
            _class.animate();
        });
    }
}

function initLiveStream(){
	 this.video = _byId('video');
     console.log("video:", this.video);
     this.canvas = _byId('canvas'); 
     this.photoReceiver = _byId("photo-receiver");
     this.init();
     this.initAnimation(this);
     this.initWebSocket();
}

function initWebSocket(){
	const _class = this;
	connectToWebsocket(null, null, null, {
		partnerId : "${partnerId}",
		callback : function(resp){
			_class.handleLiveStream(resp);
		}
		
	});
}

initLiveStream();
</script>

