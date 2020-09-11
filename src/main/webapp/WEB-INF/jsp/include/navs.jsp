
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
				<div class="side-nav-item">
					<a id="${pageItem.code }" menupage="${pageItem.isMenuPage() }"
						href="<spring:url value="${pageItem.link }"/>"><i
						class="fa fa-${pageItem.getIconClass() }" aria-hidden="false"></i>
						${pageItem.name } </a>
				</div>

			</c:forEach>

		</div>
	</div>
</div>
<script type="text/javascript">
	document.body.style.backgroundColor = "${shopProfile.color}";

	var pagesLink = document.getElementsByClassName("pagelink");
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
		try {
			postReq("<spring:url value="/api/public/pagecode" />", {},
					function(xhr) {
						infoDone();
						var response = (xhr.data);
						var pageCode = response.code;
						if (byId(pageCode)) {
							byId(pageCode).setAttribute("class",
									"nav-link pagelink active");
						}
					});
		} catch (e) {
			infoDone();
			console.log("Error occured.. when getCurrentPageCode");
		}
	}

	function initPagesLinkEvent() {
		for (let i = 0; i < pagesLink.length; i++) {
			pageLink = pagesLink[i];

			if (pageLink.getAttribute("menupage") == "true") {
				pageLink.onmouseover = function(e) {
					fetchMenus(e);
				};
			} else {
				pageLink.onmouseover = function(e) {
					hideAllMenuSpoiler();
				}
			}
		}

		byId("header-wrapper").onmouseleave = function(e) {
			hideAllMenuSpoiler();
		}
	}

	function fetchMenus(e) {
		const pageCode = e.target.id;

		if (pageMenus[pageCode] == null) {
			const url = "<spring:url value="/api/public/menus/" />" + pageCode;
			postReq(url, {}, function(xhr) {
				infoDone();
				var response = (xhr.data);
				var menus = response.entities;
				pageMenus[pageCode] = menus;
				showMenuList(pageCode);
			});
		} else {
			showMenuList(pageCode);
		}
	}

	function showMenuList(pageCode) {
		hideAllMenuSpoiler();

		const menus = pageMenus[pageCode];
		console.log("MENUS:", menus);
		const menuContainer = createGridWrapper(1, "100%");
		const parentElement = byId(pageCode).parentElement;

		if (parentElement.childElementCount > 1) {
			hideMenuByPageCode(pageCode);
		}

		menuContainer.style.width = "60%";
		menuContainer.style.textAlign = "center";
		menuContainer.setAttribute("id", "menu-spoiler-" + pageCode);
		menuContainer.setAttribute("class", "menu-spoiler");
		menuContainer.innerHTML = "<h5>Available Menu</h5>";

		for (var i = 0; i < menus.length; i++) {
			const menu = menus[i];
			const url = "<spring:url value="/"/>" + menu.url;
			const link = createAnchor(menu.code, menu.name, url);
			menuContainer.appendChild(link);
		}
		parentElement.appendChild(menuContainer);
	}

	function hideMenus(e) {
		hideMenuByPageCode(e.target.id);
	}

	function hideMenuByPageCode(code) {
		const pageLink = byId(code);
		//console.log("HIDE ", code)
		const parentElement = pageLink.parentElement;
		if (parentElement.childElementCount > 1) {
			parentElement.removeChild(parentElement.lastChild);
		}
	}

	function hideAllMenuSpoiler() {
		for (let i = 0; i < pagesLink.length; i++) {

			hideMenuByPageCode(pagesLink[i].id);
		}
	}

	//	initPagesLinkEvent();
	getCurrentPageCode();
</script>