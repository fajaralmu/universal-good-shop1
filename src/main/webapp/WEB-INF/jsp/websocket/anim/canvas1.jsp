<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
<script src="<c:url value="/res/js/player.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>
<title>Canvas Animation</title>
<style type="text/css">
canvas {
	border: 1px solid black;
}

.btn-ok {
	background-color: green;
	font-size: 2em;
	color: white
}

.btn-danger {
	background-color: red;
	font-size: 2em;
	color: white
}

.life-bar {
	border: solid 1px black;
	background-color: rgb(100, 200, 0);
	height: 20px;
	width: ${winW}px;

	

}
</style>
</head>
<body onload="disconnect()">
	<p id="info" align="center"></p>
	<h3 id="ws-info"></h3>
	
	<table style="layout:fixed">
		<tr>
			<td><h3>Health</h3><div
					style="width:${winW}px; padding:5px; border:solid 1px blue;">
					<div id="life-bar" class="life-bar"></div>
				</div></td>
			<td></td>
		</tr>
		
		<tr valign="top">
			<td>
				<canvas id="tutorial" width="${winW}" height="${winH}"> </canvas>
			</td>
			<td>
				<p id="entity-info"></p>
				<p id="realtime-info"></p>
				<p id="msg-info"></p>
			</td>
		</tr>
	
	</table>
	<label>Input Name: </label>
	<input id="name" type="text" />
	<button class="btn-ok" id="join" onclick="join()">Join</button>
	<button class="btn-ok" id="connect" onclick="connect()">Connect</button>
	<button class="btn-danger" id="leave" onclick="leave()">Leave</button>
	<hr />
	<p>
		Connected: <span id="connect-info" />
	</p>

	
	<script type="text/javascript">
		var layouts = ${layouts};

		var WIN_W = ${winW};
		var WIN_H = ${winH};
		var rolePlayer = ${rolePlayer};
		var roleBonusLife = ${roleBonusLife};
		var roleBonusArmor = ${roleBonusArmor};
		var roles = ${roles};
		var staticImages = ${staticImages};
		var baseHealth = ${baseHealth};
		var connectBtn = document.getElementById('connect');
		var canvas = document.getElementById('tutorial');
		var ctx = canvas.getContext('2d');
		var textInput = document.getElementById("draw-text");
		var initBtn = document.getElementById("animate");
		var isAnimate = false;
		var velX = 0, velY = 0;
		var x = 10, y = 10;
		var entities = new Array();
		var entity = {};
		var dirUp = "u";
		var dirLeft = "l";
		var dirRight = "r";
		var dirDown = "d";
		var entityDirection = "r";
		var firing = false;

		function printInfo(text) {
			document.getElementById("realtime-info").innerHTML = text;
		}

		function connect() {
			doConnect();
		}

		function join() {
			var name = document.getElementById("name").value;
			entity.name = name;
			postReq(
					"/websocket1/game-app-simple/join",
					"name=" + name,
					"join",
					function(response) {
						var responseObject = JSON.parse(response);
						console.log("RESPONSE", responseObject);
						if (responseObject.responseCode == "00") {
							entity = responseObject.entity;
							//	console.log("USER",entity);
							document.getElementById("entity-info").innerHTML = JSON
									.stringify(entity);
							window.document.title = "PLAYER: " + entity.name;
							document.getElementById("name").disabled = true;
							initAnimation();
							loadImages();

						} else {
							alert("FAILED :" + responseObject.responseMessage);
						}
					});
		}

		function setConnected(connected) {
			document.getElementById('connect-info').innerHTML = connected;
		}

		function leave() {
			window.document.title = "0FF-PLAYER: " + entity.name;
			leaveApp(entity.id);
		}
	</script>
	<script type="text/javascript">
		var fireCount = 0;
		var entityImages = new Array();
		var allMissiles = new Array();
		var run = 0;
		var runIncrement = 0.5;

		function updateEntityInfo() {
			var amount = this.entity.life / baseHealth * WIN_W;
			document.getElementById("life-bar").style.width = amount + "px";
		}

		window.onkeydown = function(e) {
			this.entity.physical.lastUpdated = new Date();
			move(e.key);
		}

		window.onkeyup = function(e) {
			this.entity.physical.lastUpdated = new Date();
			run = 0;
			release(e.key);
		}

		function release(key) {
			if (key == "a" || key == "d")
				velX = 0;
			if (key == "w" || key == "s")
				velY = 0;

		}

		
		function move(key) {
			if (key == "d") {
				velX = 1 + run;
				run += runIncrement;
				entityDirection = (dirRight);
			}
			if (key == "a") {
				velX = -1 - run;
				run += runIncrement;
				entityDirection = (dirLeft);
			}
			if (key == "s") {
				velY = 1 + run;
				run += runIncrement;
				entityDirection = (dirDown);
			}
			if (key == "w") {
				velY = -1 - run;
				run += runIncrement;
				entityDirection = (dirUp);
			}
			if (key == "o") {
				fireMissile();

			}
		}

		function initAnimation() {
			isAnimate = !isAnimate;
			window.requestAnimationFrame(animate);
		}

		function animate() {
			clearCanvas();
			render();
			if (isAnimate) {
				window.requestAnimationFrame(animate);
			}
		}

		function renderEntity(currentEntity) {
			var isPlayer = (currentEntity.id == this.entity.id);
			for (let i = 0; i < currentEntity.missiles.length; i++) {
				let missile = currentEntity.missiles[i];

				let velocity = getVelocity(missile.physical.direction, 5);

				currentEntity.missiles[i].physical.x += velocity.x;
				currentEntity.missiles[i].physical.y += velocity.y;

				/* for(let j=0;j<entities.length;j++){
					var u = entities[i];
					if(u.id != missile.entityId){
						if(intersect(u, missile)){
							entities[i].life--;
						}
					}
				}  */
				let missileIntersects = false;
				if (!isPlayer) {
					if (intersect(this.entity, missile).status == true) {
						firing = true;
						this.entity.life--;

					}
				}
				if (isPlayer){
					for (let x = 0; x < entities.length; x++) {
						if (entities[x].id != this.entity.id) {
							if (intersect(missile, entities[x]).status == true) {
								firing = true;
								//		console.log("===============intersects",this.entity.id,entities[i].id );
								missileIntersects = true;
							}
						}
					}
					for (let x = 0; x < layouts.length; x++) {
						 	if (intersect(missile, layouts[x]).status == true) {
								firing = true;
								//		console.log("===============intersects",this.entity.id,entities[i].id );
								missileIntersects = true;
						 }
					}	
				}

				//this works
				if (missileIntersects
						|| currentEntity.missiles[i].physical.x<0 || currentEntity.missiles[i].physical.x>WIN_W
						|| currentEntity.missiles[i].physical.y<0 || currentEntity.missiles[i].physical.y>WIN_H) {
					currentEntity.missiles.splice(i, 1);
				}
				let missilephysical = missile.physical;
				ctx.save();
				ctx.fillStyle = missilephysical.color;
				ctx.fillRect(missilephysical.x, missilephysical.y, missilephysical.w,
						missilephysical.h);
				ctx.restore();
			}

			if (isPlayer) {
				let currentphysical = currentEntity.physical;
				let outOfBounds = isOutOfBounds(currentphysical, WIN_W, WIN_H,
						velX, velY);
				let layoutItemIntersects = {};
				let intersectLayout = false;
				let intersection = {};
				let intersectionReverse = {};
				for (let i = 0; i < layouts.length; i++) {
					let layoutItem = layouts[i];
					if (!intersectLayout && intersect(currentEntity,layoutItem ).status ==true) {
						intersection  =intersect(currentEntity,layoutItem );
						intersectionReverse = intersectReverse(currentEntity,layoutItem );
						intersectLayout = true;
						layoutItemIntersects = layoutItem;
					}
				}
				
				if (intersectLayout &&( intersection.direction==currentphysical.direction 
						||
						intersectionReverse.direction==currentphysical.direction) ) {
					printInfo("intersect layout :"+ intersectionInfo+ JSON.stringify(layoutItemIntersects));
					velX = 0;
					velY = 0;
					run = 0;
				} if (intersectLayout){
					printInfo("WILL intersect layout :"+ intersectionInfo+ JSON.stringify(layoutItemIntersects));
				}else{
			 
					printInfo("NO INTERSECTION");
				}
				let velXToDo = velX;
				let velYToDo = velY;
				if(currentphysical.lastUpdate< this.entity.physical.lastUpdate){
					currentEntity.physical.x = this.entity.physical.x;
					currentEntity.physical.y = this.entity.physical.y;
					velXToDo = 0;
					velYToDo = 0;
					run = 0;
				}
				
				if (!outOfBounds) {
					currentEntity.physical.x += velXToDo;
					currentEntity.physical.y += velYToDo;
				}
				this.entity.physical.direction = entityDirection;
				currentEntity.physical.direction = this.entity.physical.direction;
				currentEntity.life = this.entity.life;
				//currentEntity.missiles = this.entity.missiles;
				this.entity = currentEntity;
				document.getElementById("entity-info").innerHTML = JSON
						.stringify(this.entity);
				updateEntityInfo();
			}
			if (velX != 0 || velY != 0 || currentEntity.missiles.length > 0
					|| firing) {
				//console.log("=================",currentEntity.physical);
				if (firing)
					firing = false;
				updateMovement();
			}

			let physical = currentEntity.physical;
		 	ctx.save();
		 	ctx.fillStyle = physical.color;
			ctx.font = "15px Arial";
			
			if (!currentEntity.physical.layout) {
				ctx.fillText(currentEntity.name + "." + physical.direction + "."
						+ currentEntity.active + ".(" + currentEntity.life + ")",
						physical.x, physical.y - 10);
			} else {
				 ctx.fillText(currentEntity.id, physical.x, physical.y - 10);

			}//ctx.strokeRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
			//ctx.fillRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
			ctx.drawImage(getEntityImage(currentEntity.physical.role,
					currentEntity.physical.direction), physical.x, physical.y,
					currentEntity.physical.w, currentEntity.physical.h);
			fireCount++;
			ctx.restore();

		}

		function fireMissile() {
			if (fireCount < 20) {
				return;
			}
			firing = true;
			fireCount = 0;
			var missile = createMissile(this.entity);
			console.log("000000000000000000000000000000Fire Missile", missile);
			this.entity.missiles.push(missile);
			updateMovement();
		}

		function getEntityImage(role, dir) {
			var fullAddress = window.location.protocol + '//'
					+ window.location.hostname
					+ (window.location.port ? ':' + window.location.port : '');
			let url = fullAddress + "<c:url value="/res/img/player/"/>"
					+ getDirImage(role, dir);
			for (var i = 0; i < entityImages.length; i++) {
				if (entityImages[i].src == url) {
					return entityImages[i];
				}
			}
			return new Image();

		}

		function loadImages() {
			let urls = new Array();
			for (let i = 0; i < roles.length; i++) {
				let role = roles[i];
				urls .push("<c:url value="/res/img/player/"/>" + role
								+ "_u.png");
				urls .push("<c:url value="/res/img/player/"/>" + role
								+ "_d.png");
				urls .push("<c:url value="/res/img/player/"/>" + role
								+ "_r.png");
				urls .push("<c:url value="/res/img/player/"/>" + role
								+ "_l.png");
			}
			for (let i = 0; i < staticImages.length; i++) {
				let staticImage = staticImages[i];
				urls .push("<c:url value="/res/img/"/>" + staticImage); 
			}

			for (let i = 0; i < urls.length; i++) {
				var image = new Image();

				image.onload = function() {
					console.log("Image loaded: ", urls[i], image);
					ctx.drawImage(image, i * 50, 0, 50, 38);
				}
				image.src = urls[i];
				entityImages.push(image);
			}
		}

		function render() {
			for (let i = 0; i < layouts.length; i++) {
				let currentLayout = layouts[i];
				renderEntity(currentLayout);
			}

			for (let i = 0; i < entities.length; i++) {
				let currentEntity = entities[i];
				renderEntity(currentEntity);

				if (currentEntity.physical.role == 101
						&& currentEntity.id != this.entity.id) {
					if (intersect(this.entity, currentEntity).status == true) {
						if (this.entity.life < baseHealth) {
							this.entity.life += currentEntity.life;
							if (this.entity.life > baseHealth) {
								this.entity.life = baseHealth
							}
							updateMovement();
						}
						leaveApp(currentEntity.id);
					}
				}
			}
		}

		function draw() {
			if (canvas.getContext) {
				ctx.beginPath();
				ctx.arc(70, 80, 10, 0, 2 * Math.PI, false);
				ctx.fill();
			} else {
				alert("Not Supported");
			}
		}

		function clearCanvas() {
			ctx.clearRect(0, 0, canvas.width, canvas.height);
		}
		draw();
	</script>
</body>
</html>