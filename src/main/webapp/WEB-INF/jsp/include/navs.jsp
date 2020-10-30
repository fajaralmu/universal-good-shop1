
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div class="header" style="height: auto">
	<div>
	<c:if test="${loggedUser!=null }">
	<div class="profile-thumbnail " style="width:100%; text-align: center; color: ${shopProfile.fontColor}">
		<div  
		style="
			background-position: center;
			background-repeat: no-repeat;
			background-size: cover;
		 	background-image: url('${host}/${contextPath}/${imagePath}/${loggedUser.profileImage }'); 
		 	width: 100px;
		 	height: 100px;
		 	border-radius: 50px;
		 	margin: auto;
		" ></div>
		<p>${loggedUser.displayName}</p>
	</div>
	</c:if>
		
		<div class="nav-list">
			<c:forEach var="pageItem" items="${pages}">
				<div class="side-nav-item" id="${pageItem.code }">
					<a class="nav-link side-link" href='#' page-code='${pageItem.code }' menupage="${pageItem.isMenuPage() }" location="<spring:url value="${pageItem.link }"/>">
						<i class="${pageItem.getIconClass() }" aria-hidden="false"></i> ${pageItem.name } 
					</a>
				</div>
			</c:forEach>

		</div>
	</div>
</div>
<script type="text/javascript">
	document.body.style.backgroundColor = "${shopProfile.color}";

	const pagesLink = document.getElementsByClassName("side-nav-item");
	const navLinks = document.getElementsByClassName("side-link");
	const MART_SIDEBAR_CODE = '${shopProfile.id}_mart_sidebarcode';
	var pageMenus = {};
	var ctxPath = "${contextPath}";
	function logout() {
		postReq(
				"<spring:url value="/api/account/logout" />",
				{},
				function(xhr) {
					infoDone();
					var response = (xhr.data);
					if (response != null && response.code == "00") {

						window.location.href = "<spring:url value="/account/login" />";
					} else {
						alert("LOGOUT FAILS");
					}
				});
	}

	function getCurrentPageCode() {
		/* try {
			postReq("<spring:url value="/api/public/pagecode" />", {},
					function(xhr) {
						infoDone();
						var response = (xhr.data);
						var pageCode = response.code;
						if (byId(pageCode)) {
							byId(pageCode).className = "nav-link pagelink active";
						}
					});
		} catch (e) {
			infoDone();
			console.log("Error occured.. when getCurrentPageCode");
		} */
	}

	function initPagesLinkEvent() {
		for (var i = 0; i < pagesLink.length; i++) {
			const pl = pagesLink[i];
			console.debug("pagesLinkID:", pl.id," vs ",getSideBarCodeCookie());
			if(pl.id == getSideBarCodeCookie()){
				pl.className+=' active';
			}
		}
		for (var i = 0; i < navLinks.length; i++) {
			const navLink = navLinks[i];
			const location = navLink.getAttribute("location");
			const code = navLink.getAttribute("page-code");
			navLink.onclick = function(e){
				e.preventDefault();
				setSideBarCookie(code, location);
			}
			navLink.onmousedown = function(e){
                if (event.which == 3) {
                	navLink.setAttribute("href", location);
                   // alert("right clicked!");
                }
            }
			navLink.onmouseout = function(e){
				navLink.setAttribute("href", "#");
            }
		}
	}

	function fetchMenus(e) {
		const pageCode = e.target.id;

		if (pageMenus[pageCode] == null) {
			const url = "<spring:url value="/api/public/menus/" />" + pageCode;
			postReq(url, {}, function(xhr) {
				infoDone();
				const response = (xhr.data);
				const menus = response.entities;
				pageMenus[pageCode] = menus;
				showMenuList(pageCode);
			});
		} else {
			showMenuList(pageCode);
		}
	}
	
	function setCookie(key, value){
		return new Promise(function (res, rej){localStorage.setItem( key, value); res(true) });
	}

	function getSideBarCodeCookie(){
		return localStorage.getItem(MART_SIDEBAR_CODE);
//		return getCookie(MART_SIDEBAR_CODE);
	}
	
	function setSideBarCookie(code, link){
		setCookie(MART_SIDEBAR_CODE, code).then(function(e){
			console.info("sidebar code: "+ getSideBarCodeCookie());
			window.location.href = link;
		})
		
	}

	initPagesLinkEvent();
	getCurrentPageCode();
</script>