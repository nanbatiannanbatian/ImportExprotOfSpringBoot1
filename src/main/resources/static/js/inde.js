function myGetDate(i){
            var date = new Date();
            var num=0;
            var choiceBar = document.getElementById("pagesA");
            var article = document.getElementById("content");
            //动态的往ul里面插入数据,这个可以从数据库里取出的数据，我本来想弄个时间戳的，功夫不到家，将就的看吧.
            while(i){
                $("#content").append("<li> Year : " + date.getFullYear() + " Month :" + date.getMonth() + " Day : " + date.getDate() + " hour : " + date.getHours() + " minute : " + date.getMinutes() + " second : " + i) + "</li>";
                i--;
            }
            //动态的生成分页按钮的个数
            var len = $("#content li").length;
            for(var i=0; i<len/10; i++){
                $("#pagesA").append("<li style='width:24px;height:24px;display:inline-block;border: 1px solid #D611EA;text-align:center;line-height:24px;'>" + (i+1) + "</li>");
            }
            $("#pagesA li:first-child").addClass("cur");
            //给每个按钮添加事件
            for(var i=0; i<choiceBar.children.length; i++){
                choiceBar.children[i].index = i;
                choiceBar.children[i].onclick = function(){
                    for(var j=0; j<choiceBar.children.length; j++){
                        choiceBar.children[j].className = "";
                    }
                    this.className = "cur";
                    num = this.index;
                    init(num);
                }
            }
            //上一个按钮添加事件
            $("#pageP").click(function(){
                if(num==0){                                     //到第一页的情况
                    return false;
                }else{
                    for(var j=0; j<choiceBar.children.length; j++){
                        choiceBar.children[j].className = "";
                    }
                    choiceBar.children[num-1].className = "cur";
                    init(num-1);
                    return num--;
                }
            });
            //下个按钮添加事件
            $("#pageN").click(function(){
                if(num==choiceBar.children.length-1){             //到最后一页的情况
                    return false;
                }else{
                    for(var j=0; j<choiceBar.children.length; j++){
                        choiceBar.children[j].className = "";
                    }
                    choiceBar.children[num+1].className = "cur";
                    init(num+1);
                    return num++;
                }
            });
            //显示与按钮对应的十个信息
            function init(myNum){
                for(var j=0; j<choiceBar.children.length; j++){
                    for(var i=0; i<10; i++){
                        if(article.children[(j*10)+i] == undefined){
                            continue;
                        }
                        article.children[(j*10)+i].style.display = "none";
                    }    
                }
                for(var i=0; i<10; i++){
                    if(article.children[(myBum*10)+i] == undefined){
                        continue;
                    }
                    article.children[(myBum*10)+i].style.display = "block";
                }
            }
            init(0);
        }