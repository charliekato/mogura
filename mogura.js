/*
   */
//var	counter;
var	canvasWidth=310;
var	canvasHeight=400;
var 	mouseX;
var 	mouseY;
var	moguraWidth=102;
var	moguraHeight=102;
var	topLeftX = 1; 
var	topLeftY = 25; 
var	fontsize = 16;

var	initMoguCounter=50;


//var	startTime;
//var	endTime;

var	difficultyIndex=0;

var	numMoguraX=3;
var	numMoguraY=3;
var	numMogura=numMoguraX*numMoguraY;   
var	moguState = new Array(numMogura);
var	score=0;
var	debugArea;
var	gameStatus=0;
var	emergenceRate;
var  	withdrawRate = new Array(numMogura);
var	proceedRate  = new Array(numMogura);
var	moguCounter ;
var	baseTime;
var 	numEmerged = 0;

var	canvasElement;
var 	ctx;

var	img0 = new Image();
var	img1 = new Image();
var	img2 = new Image();
var	img3 = new Image();
var	img4 = new Image();

img0.src = "images/mogura0.gif";
img1.src = "images/mogura1.gif";
img2.src = "images/mogura2.gif";
img3.src = "images/mogura3.gif";
img4.src = "images/mogura4.gif";


function getRandom(maxNum) {
    return parseInt(Math.random() * maxNum );
}

function decScore(decValue) {
    score -=decValue;
    if (score<0) score=0;
    printScore();
}

function incScore(incvalue) {
    score += incvalue;
    printScore();
}

function printScore() {
    var		prefix="";
    if ( score<100) prefix=prefix+"0";
    if ( score<10)  prefix=prefix+"0";
    $('#score').text(prefix+score); // jquery
//    document.getElemnetById("score").innerHTML = score;
}

function determinEmergenceRate() {
    if (difficultyIndex == 0 ) {
	if (numEmerged>3) {
	    emergenceRate=0.03;
	    return;
	}
	if (numEmerged>2) {
	    emergenceRate=0.06;
	    return;
	}
	if (numEmerged>1) {
	    emergenceRate=0.2;
	    return;
	}
	if (numEmerged==0) {
	    emergenceRate=0.5;
	    return;
	}
    } else if (difficultyIndex == 1) {
	if (numEmerged>3) {
	    emergenceRate=0.2;
	    return;
	}
	if (numEmerged>2) {
	    emergenceRate=0.3;
	    return;
	}
	if (numEmerged>1) {
	    emergenceRate=0.4;
	    return;
	}
	if (numEmerged==0) {
	    emergenceRate=0.5;
	    return;
	}

    } else {
	if (numEmerged>3) {
	    emergenceRate=0.5;
	    return;
	}
	if (numEmerged>2) {
	    emergenceRate=0.5;
	    return;
	}
	if (numEmerged>1) {
	    emergenceRate=0.5;
	    return;
	}
	if (numEmerged==0) {
	    emergenceRate=0.6;
	    return;
	}

    }
}

//window.addEventListener("load",initialize,false);

function initialize() {
    debugArea=document.getElementById("debugArea");
    canvasElement=document.getElementById("testCanvas");
    ctx = canvasElement.getContext('2d');

    ctx.font = ""+fontsize+"pt 'Courier'";


    registerEvent();
    for (var i=0; i<numMoguraY; i++) {
	for (var j=0; j<numMoguraX; j++) {
	    var		x=topLeftX + i*moguraWidth;
	    var		y=topLeftY + j*moguraHeight;
	    ctx.drawImage(img0, 0, 0, 100, 100, x, y, 100, 100);
	}
    }
}


function windowInitialize() {
    for (var i=0; i<numMoguraY; i++) {
	for (var j=0; j<numMoguraX; j++) {
	    var		x=topLeftX + j*moguraWidth;
	    var		y=topLeftY + i*moguraHeight;
	    ctx.drawImage(img0, 0, 0, 100, 100, x, y, 100, 100);
	}
    }
}

function initializeMoguraState() {
    for (var i=0; i < numMogura; i++ ) { 
	moguState[i]=0; 
	resetRate(i);
    }
}



function resetRate(moguraNo) {
    withdrawRate[moguraNo] = 0.07;
    proceedRate[moguraNo] = 0.8;
}
function increaseWithdrawRate(moguraNo) {
    withdrawRate[moguraNo] = 0.14;
    proceedRate[moguraNo] = 0.8;
}

function setRatetoWithdraw(moguraNo) {
    withdrawRate[moguraNo] = 0.8;
    proceedRate[moguraNo]  = 0.8;
}



function registerEvent() {

    canvasElement.addEventListener("contextmenu", function(e){ e.preventDefault();}, false);
    canvasElement.onmousedown = mouseClicked;
}

    
function getRowNo( number ) {
    if (number<3) return 0;
    if (number<6) return 1;
    return 2;
}
function getColumnNo(number ) {
    if (number==0) return 0;
    if (number==1) return 1;
    if (number==2) return 2;
    if (number==3) return 0;
    if (number==4) return 1;
    if (number==5) return 2;
    if (number==6) return 0;
    if (number==7) return 1;
    if (number==8) return 2;
    return -1;
}
    

function whichOneToEmerge() {
    determinEmergenceRate();
    var		ransuu=Math.random();
//    debugPrint(1,"ransuu --> " + ransuu );
    if (ransuu< emergenceRate ) {
	var		whichOne=getRandom(numMogura);
	if ( moguState[whichOne] ==0) return whichOne;
    }
    return -1;
}



function did_i_hit(x, y ) {
    var  	mogunum;
    var		i,j;
    var		minx, miny, maxx, maxy;
    for ( mogunum=0; mogunum< numMogura; mogunum++) {
	j = getRowNo(mogunum) ;
	i = getColumnNo(mogunum);
	minx = topLeftX+moguraWidth*i+5;
	maxx = topLeftX+moguraWidth*i+95;
	miny = topLeftY+moguraHeight*j+30;
	maxy = topLeftY+moguraHeight*j+80;
	if (( x>=minx ) && (x<=maxx) && ( y>=miny) && (y<=maxy) )
	    return mogunum;
    }
    return -1;
}
	
	
	

function mouseClicked(e) {
    var		moguraNo=-1;
    if (gameStatus!=1) return;
    getXY(e);
    moguraNo = did_i_hit(mouseX, mouseY);
//    debugPrint(" mouse clicked @ ("+ mouseX +" , "+mouseY + ")" + "moguraNo is " + moguraNo);
    if ( moguraNo>=0) {
//	debugPrint( "mogura state --> " + moguState[moguraNo]);
	if ( (moguState[moguraNo] < 4)&&(moguState[moguraNo] >0)) {
	    incScore(5);
	    moguState[moguraNo]=4;
	    changeImage(moguraNo, img4);
	    setTimeout("resetMogu("+moguraNo+")",700);
	    printScore();
	}
    } 
}

function getXY(e) {
    var rect = canvasElement.getBoundingClientRect();
    mouseX = e.clientX - rect.left;
    mouseY = e.clientY - rect.top;

}


function withdrawMogu(moguraNo) {
    if (gameStatus != 1) return;
    moguState[moguraNo]--;
   
    if (moguState[moguraNo] > 0) {
	setRatetoWithdraw(moguraNo);
        if (moguState[moguraNo] == 1) {
	    changeImage(moguraNo, img1);
	}
	else if (moguState[moguraNo] == 2) {
	    changeImage(moguraNo, img2);
	} 

    } else {
	changeImage(moguraNo, img0);
	resetRate(moguraNo);
	numEmerged--;
    }
}

function proceedMogu(moguraNo) {
    
    if (gameStatus != 1) return;
    moguState[moguraNo]++;
    if (moguState[moguraNo] == 2 ) {
	increaseWithdrawRate(moguraNo);
	changeImage(moguraNo, img2);
    }
    if (moguState[moguraNo] == 3) {
	setRatetoWithdraw(moguraNo);
	changeImage(moguraNo, img3);
    }

}

function resetMogu(moguraNo) {
//    debugPrint("gameStatus ==> "+gameStatus);
    if (moguState[moguraNo] == 4 ) {
	if ( gameStatus == 1) {
	    numEmerged--;
	    moguState[moguraNo] =0;
	    resetRate(moguraNo);
	    changeImage(moguraNo, img0);
	} else if (gameStatus==2) {

	    setTimeout("resetMogu("+moguraNo+")", 100);

	}
    }

}

function updateMogura() {
    var		i;
    for (i=0; i<numMogura; i++) {
	if ( (moguState[i]<4)&&(moguState[i] > 0 )) {
	    ransuu=Math.random();
	    if (ransuu<withdrawRate[i]) {
		withdrawMogu(i);
	    } else if (ransuu<proceedRate[i]) {
		proceedMogu(i);
	    }
	}
    }
}


function changeImage(moguraNo,image) {
    var i =  getColumnNo(moguraNo);
    var j =  getRowNo(moguraNo);
    var	x=topLeftX + i*moguraWidth;
    var	y=topLeftY + j*moguraHeight;
    ctx.drawImage(image, 0, 0, 100, 100, x, y, 100, 100);
}
function emerge(moguraNo) {
    var		i,j;
    
    numEmerged++;
    moguState[moguraNo] = 1;
    changeImage(moguraNo, img1);
    decrimentMogura();
}

function decrimentMogura() {
    
    moguCounter--;
    if (moguCounter==35) {
	baseTime -= 30;
	gameStatus=2;
	printIntResult();
    }
    if (moguCounter==25) {
	baseTime -= 35;
	gameStatus=2;
	printIntResult2();
    }
    if (moguCounter==15) baseTime -= 40;
    
    if (moguCounter==0) {
	gameStatus=-1;
	gameOver();
    }
}

function timerBreak() {
//    counter++;
//    debugPrint(2, "counter  -->" + counter );
    if (gameStatus==1) {
	updateMogura();
	var	whichMogura= whichOneToEmerge();
	if (whichMogura>=0) emerge(whichMogura);
    }
    if (gameStatus>0)
    setTimeout("timerBreak()", baseTime);
    
}

function startGame() {
    if (gameStatus<=0) {
	$('#startButton').toggleClass('hide');
	difficultyIndex=0;
	numEmerged=0;

	
	score=0;
	emergenceRate=0.5;
	numEmerged = 0;
	moguCounter = initMoguCounter;
	baseTime = 350;
	gameStatus=1;
	setTimeout("timerBreak()", baseTime);
	windowInitialize();
	initializeMoguraState();
	printScore();
    }
}


function printIntResult() {
    var msg;
    var msg2="";
    var		fullScore=(initMoguCounter-moguCounter)*5;
    if (score<=fullScore*0.4) {
	msg="かっかっか！";
    } else if (score<=fullScore*0.5) {
	msg="まだまだだね。";
    } else if (score<=fullScore*0.7) {
	msg="まぁまぁかな。";
	msg2="ちょっと速くなるよ";

    } else if (score<fullScore*0.9) {
	msg="なかなかやるね！";
	msg2="本気出したる。";
	baseTime -= 30;
    } else {
	msg="むか。見てろ！";
	msg2="これでどうだ！";
	baseTime -= 45;
	difficultyIndex=1;
    }
   
    $('#popup').html(msg);
    $('#popup2').html(msg2);

    setTimeout("removePopUp()", 1100);
    
}

function removePopUp() {
    $('#popup').html('');
    $('#popup2').html('');
    gameStatus--;

}


function printIntResult2() {
    var msg;
    var msg2="";
    var		fullScore=(initMoguCounter-moguCounter)*5;
    if (score<=fullScore*0.4) {
	msg="かっかっか！";
	msg2="へったくそ！";
    } else if (score<=fullScore*0.5) {
	msg="まだまだだね。";
	msg2="修業が足りんね。";
    } else if (score<=fullScore*0.7) {
	msg="まぁまぁかな。";
    } else if (score<fullScore*0.9) {
	msg="なかなかやるね！";
	msg2="本気だすか。";
	difficultyIndex=1;
	baseTime -= 30;
    } else {
	msg="もう、怒ったぞ！";
	difficultyIndex=2;
	baseTime -= 60;
    }
    $('#popup').html(msg);
    $('#popup2').html(msg2);
    setTimeout("removePopUp()", 1100);
}


function gameOver() {
//    var     DD = new Date();
//    endTime=DD.getTime();
//    var elapsedTime = endTime-startTime;
//    debugPrint(4,"end time : "+endTime+"  elapsed time ==> "+elapsedTime);
    var msg="Game Over";
    var msg2="";
    var		fullScore=(initMoguCounter-moguCounter)*5;
    if (score<=fullScore*0.4) {
	msg2="出直して来な！";
    } else if (score<=fullScore*0.5) {
	msg2="修業が足りんね。";
    } else if (score<=fullScore*0.7) {
	msg2="まぁまぁかな。";
    } else if (score<fullScore*0.9) {
	msg2="なかなかやるね！";
    } else {
	msg2="まいりました！";
    }
    $('#popup').html(msg);
    $('#popup2').html(msg2);
    $('#startButton').toggleClass('hide');
    setTimeout("removePopUp()", 5000);

}




function debugPrint(num,msg) {
    $('#debugArea'+num).html(msg);
}

function buttonClicked() {
    $('#header .leftButton').toggleClass('pressed');
}

function getBrowserWidth ( ) {
    if ( window.innerWidth ) { return window.innerWidth; }
    else if ( document.documentElement && document.documentElement.clientWidth != 0 ) { return document.documentElement.clientWidth; }
    else if ( document.body ) { return document.body.clientWidth; }
	return 0;
}
