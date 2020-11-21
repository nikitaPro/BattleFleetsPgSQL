<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<header>
        <p>${login}</p>
        <p>Level ${level}/${maxLvl}</p>
        <c:if test="${level!=maxLvl}">
        <p>Points ${points}/${toNxtLevel}</p>
        </c:if>
        <p>Money <span id="money">${money}</span></p>
        <p>Fleet ${currShips}/<span id="maxShips">${maxShips}</span></p>
        <p>Income <span id="income">${income}</span>/day</p>
        <c:if test="${nextImprove<=maxLvl}">
        <p id="improveWrapper">Improve <span  id="improve">${nextImprove}</span> lvl</p>
        </c:if>
</header>

