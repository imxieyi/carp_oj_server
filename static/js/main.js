	'use strict';

	// var RootUrl = "http://10.20.34.203:8080";
	var RootUrl = "";
	var StatusCode = ["WAITING", "QUEUED", "RUNNING", "FINISHED", "ERROR"];
	var DataSetList = [];
	var RankData = [];
	var Page = {
	  each_num: 13,
	  items_num: 0,
	  page_num: 0,
	  items: [],
	  now_page: 0, //Where are you.
	  page_html: "",
	};
	var RenderRankList;
	var getSubmitResult;
	//Global function for Dom Click events
	var logout_user = function() {
	  console.log("logout");
	  $.ajax({
	    url: RootUrl + "/api/logout",
	    type: 'POST',
	    headers: {
	      "X-XSRF-TOKEN": $.cookie("XSRF-TOKEN")
	    },
	    contentType: 'application/json',
	    async: false,
	    success: function(data) {
	      if (typeof data == "string") {
	        data = JSON.parse(data);
	      }
	      if (data["uid"]) {
	        $("#info_box_msg").html("<p>Logout!</p>")
	        $("#info_box").modal("show");
	        $.cookie("username", null);
	        $.cookie("userid", null);
	        setTimeout(function() {
	          $("#info_box").modal("hide");
	          window.location.href = "index.html";
	        }, 2000);
	      }
	    }
	  });
	}
	var changeRankTab = function(e) {
	  let aimid = $(e).attr("id");
	  $("#rank_tab_list").find("li").each(function(i, e) {
	    if ($(e).find("a").attr("id") != aimid) {
	      $(e).removeClass("active");
	    } else {
	      $(e).removeClass("active");
	      $(e).addClass("active");
	    }
	  });
	  RenderRankList(aimid);
	}
	var change_page = function(id) {
	  Page.now_page = id;
	  Page.page_html = "";
	  let page_active = "";
	  let user_rank_page_html = "";
	  for (var i = 0; i < Page.page_num; i++) {
	    if (i == Page.now_page) {
	      page_active = "page_active";
	    } else {
	      page_active = "";
	    }
	    Page.page_html += "<button class='btn btn-default btn-sm " + page_active + "' onclick='change_page(" + i + ")'>" + (parseInt(i) + 1) + "</button>";
	  }
	  console.log(Page.now_page * Page.each_num, (((Page.now_page + 1) * Page.each_num) > Page.items_num ? Page.items_num : (Page.now_page + 1) * Page.each_num))
	  for (var index = Page.now_page * Page.each_num; index < (((Page.now_page + 1) * Page.each_num) > Page.items_num ? Page.items_num : (Page.now_page + 1) * Page.each_num); index++) {
	    user_rank_page_html += Page.items[index];
	  }
	  user_rank_page_html += "<tr class='active' style='text-align:center;'><td colspan='5'>" +
	    // "<button class='btn btn-info btn-sm' id='page-left'><i class='glyphicon glyphicon-arrow-left'></i></button>" +
	    Page.page_html +
	    // "<button class='btn btn-info btn-sm' id='page-right'><i class='glyphicon glyphicon-arrow-right'></i></button>" +
	    "</td></tr>";
	  console.log($("#page_box"))
	  $("#page_box").empty();
	  $('#page_box').append(user_rank_page_html);
	}
	var addzero = function(s, n) {
	  //在首部补0
	  s = String(s);
	  if (n == undefined) n = 2;
	  n = parseInt(n, 10);
	  if (isNaN(n)) n = 0;
	  if (s.length < n) {
	    s = "0" + s;
	    return addzero(s, n)
	  }
	  if (s.length >= n) {
	    return s;
	  }
	  return s;
	};
	(function() {
	  var isMobile = {
	    Android: function() {
	      return navigator.userAgent.match(/Android/i);
	    },
	    BlackBerry: function() {
	      return navigator.userAgent.match(/BlackBerry/i);
	    },
	    iOS: function() {
	      return navigator.userAgent.match(/iPhone|iPad|iPod/i);
	    },
	    Opera: function() {
	      return navigator.userAgent.match(/Opera Mini/i);
	    },
	    Windows: function() {
	      return navigator.userAgent.match(/IEMobile/i);
	    },
	    any: function() {
	      return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
	    }
	  };
	  var mobileMenuOutsideClick = function() {

	    $(document).click(function(e) {
	      var container = $("#fh5co-offcanvas, .js-fh5co-nav-toggle");
	      if (!container.is(e.target) && container.has(e.target).length === 0) {

	        if ($('body').hasClass('offcanvas')) {

	          $('body').removeClass('offcanvas');
	          $('.js-fh5co-nav-toggle').removeClass('active');

	        }


	      }
	    });

	  };
	  var offcanvasMenu = function() {

	    $('#page').prepend('<div id="fh5co-offcanvas" />');
	    $('#page').prepend('<a href="#" class="js-fh5co-nav-toggle fh5co-nav-toggle fh5co-nav-white"><i></i></a>');
	    var clone1 = $('.menu-1 > ul').clone();
	    $('#fh5co-offcanvas').append(clone1);
	    var clone2 = $('.menu-2 > ul').clone();
	    $('#fh5co-offcanvas').append(clone2);
	    $('#fh5co-offcanvas .has-dropdown').addClass('offcanvas-has-dropdown');
	    $('#fh5co-offcanvas')
	      .find('li')
	      .removeClass('has-dropdown');

	    // Hover dropdown menu on mobile
	    $('.offcanvas-has-dropdown').mouseenter(function() {
	      var $this = $(this);

	      $this
	        .addClass('active')
	        .find('ul')
	        .slideDown(500, 'easeOutExpo');
	    }).mouseleave(function() {

	      var $this = $(this);
	      $this
	        .removeClass('active')
	        .find('ul')
	        .slideUp(500, 'easeOutExpo');
	    });


	    $(window).resize(function() {

	      if ($('body').hasClass('offcanvas')) {

	        $('body').removeClass('offcanvas');
	        $('.js-fh5co-nav-toggle').removeClass('active');

	      }
	    });
	  };
	  var burgerMenu = function() {

	    $('body').on('click', '.js-fh5co-nav-toggle', function(event) {
	      var $this = $(this);


	      if ($('body').hasClass('overflow offcanvas')) {
	        $('body').removeClass('overflow offcanvas');
	      } else {
	        $('body').addClass('overflow offcanvas');
	      }
	      $this.toggleClass('active');
	      event.preventDefault();

	    });
	  };
	  var fullHeight = function() {

	    if (!isMobile.any()) {
	      $('.js-fullheight').css('height', $(window).height());
	      $(window).resize(function() {
	        $('.js-fullheight').css('height', $(window).height());
	      });
	    }

	  };
	  var contentWayPoint = function() {
	    var i = 0;
	    $('.animate-box').waypoint(function(direction) {

	      if (direction === 'down' && !$(this.element).hasClass('animated-fast')) {

	        i++;
	        $(this.element).addClass('item-animate');
	        setTimeout(function() {

	          $('body .animate-box.item-animate').each(function(k) {
	            var el = $(this);
	            setTimeout(function() {
	              var effect = el.data('animate-effect');
	              if (effect === 'fadeIn') {
	                el.addClass('fadeIn animated-fast');
	              } else if (effect === 'fadeInLeft') {
	                el.addClass('fadeInLeft animated-fast');
	              } else if (effect === 'fadeInRight') {
	                el.addClass('fadeInRight animated-fast');
	              } else {
	                el.addClass('fadeInUp animated-fast');
	              }

	              el.removeClass('item-animate');
	            }, k * 200, 'easeInOutExpo');
	          });

	        }, 100);

	      }

	    }, {
	      offset: '85%'
	    });
	  };
	  var dropdown = function() {

	    $('.has-dropdown').mouseenter(function() {

	      var $this = $(this);
	      $this
	        .find('.dropdown')
	        .css('display', 'block')
	        .addClass('animated-fast fadeInUpMenu');

	    }).mouseleave(function() {
	      var $this = $(this);

	      $this
	        .find('.dropdown')
	        .css('display', 'none')
	        .removeClass('animated-fast fadeInUpMenu');
	    });

	  };
	  var goToTop = function() {
	    $('.js-gotop').on('click', function(event) {

	      event.preventDefault();

	      $('html, body').animate({
	        scrollTop: $('html').offset().top
	      }, 500, 'easeInOutExpo');

	      return false;
	    });

	    $(window).scroll(function() {

	      var $win = $(window);
	      console.log($win.scollTop())
	      if ($win.scrollTop() > 200) {
	        $('.js-top').addClass('active');
	      } else {
	        $('.js-top').removeClass('active');
	      }

	    });

	  };
	  // Loading page
	  var loaderPage = function() {
	    $(".fh5co-loader").fadeOut("slow");
	  };
	  var counterWayPoint = function() {
	    if ($('#fh5co-counter').length > 0) {
	      $('#fh5co-counter').waypoint(function(direction) {

	        if (direction === 'down' && !$(this.element).hasClass('animated')) {
	          setTimeout(counter, 400);
	          $(this.element).addClass('animated');
	        }
	      }, {
	        offset: '90%'
	      });
	    }
	  };
	  var sliderMain = function() {

	    $('#fh5co-hero .flexslider').flexslider({
	      animation: "fade",
	      slideshowSpeed: 5000,
	      directionNav: true,
	      start: function() {
	        setTimeout(function() {
	          $('.slider-text').removeClass('animated fadeInUp');
	          $('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
	        }, 500);
	      },
	      before: function() {
	        setTimeout(function() {
	          $('.slider-text').removeClass('animated fadeInUp');
	          $('.flex-active-slide').find('.slider-text').addClass('animated fadeInUp');
	        }, 500);
	      }

	    });

	    $('#fh5co-hero .flexslider .slides > li').css('height', $(window).height());
	    $(window).resize(function() {
	      $('#fh5co-hero .flexslider .slides > li').css('height', $(window).height());
	    });

	  };

	  var isLogin = function() {
	    let userid = $.cookie("userid");
	    let uname = $.cookie("username");
	    let login_bool = (userid && uname && uname != 'null' && uname != null && uname != "undefined" && uname != undefined && uname.length > 0 && userid.length > 0);
	    return login_bool;
	  }
	  var checkCookie = function() {
	    if (isLogin()) {
	      $(".user_place").each(function(i, e) {
	        $(e).css("display", "none");
	      });
	      $(".user_span").each(function(i, e) {
	        let user = $.cookie("username");
	        $(e).empty();
	        $(e).append("<a href='#'  class='pop_logout' onclick='logout_user()'><span id='logout'>&nbsp;&nbsp;Hello," + user + "&nbsp;&nbsp;</span></a>");
	        $(e).css("display", "inline-block");

	        $(".pop_logout").mouseover(function(event) {
	          let remain = $(this).html();
	          $(this).html("<span class='logout_span'>&nbsp;&nbsp; Logout &nbsp;&nbsp;</span>");
	          let self = this;
	        });
	        $(".pop_logout").mouseleave(function(event) {
	          $(this).html("<span id='logout'>&nbsp;&nbsp;Hello," + user + "&nbsp;&nbsp;</span>");
	        });
	        $("#logout_span").click(function() {
	          console.log("...");
	          logout_user()
	        });
	      });
	      $(".change_pwd").each(function(i, e) {
	        $(e).css("display", "inline-block");
	      });
	      //登录之后拉取提交记录
	      getSubmitResult(0);
	      //登录之后拉取提交剩余次数
	      getSubmitRemain();
	      getSubmitQueue();
	    }
	  }
	  var getDatasetList = function() {
	    $.ajax({
	      url: RootUrl + '/api/dataset/all',
	      type: 'GET',
	      contentType: 'application/json',
	      async: false,
	      success: function(data) {
	        if (typeof(data) == "string") {
	          data = JSON.parse(data);
	        }
	        DataSetList = data["datasets"];
	        RenderOptions(data["datasets"]);
	      }
	    });
	  }
	  var RenderOptions = function(dataset_list) {
	    var SelectHtml = '<select class="form-control" id="graph_choose" name="group_select">';
	    for (var i in dataset_list) {
		  var typeCode = dataset_list[i]["type"];
		  var typeName = "INVALID";
		  if (typeCode === 0) {
		  	typeName = "CARP";
		  } else if (typeCode === 1) {
		  	typeName = "ISE";
		  } else if (typeCode === 2) {
		  	typeName = "IMP";
		  }
	      SelectHtml += "<option value='" + dataset_list[i]["id"] + "'>" + typeName + ": " + dataset_list[i]["name"] + "</option>";
	    }
	    SelectHtml += "</select>";
	    $("#dataset_option").empty();
	    $("#dataset_option").append(SelectHtml);

	    var SelectHtml = '<select class="form-control" id="graph_choose1" name="group_select">';
	    for (var i in dataset_list) {
            var typeCode = dataset_list[i]["type"];
            var typeName = "INVALID";
            if (typeCode === 0) {
                typeName = "CARP";
            } else if (typeCode === 1) {
                typeName = "ISE";
            } else if (typeCode === 2) {
                typeName = "IMP";
            }
	      SelectHtml += "<option value='" + dataset_list[i]["id"] + "'>" + typeName + ": " + dataset_list[i]["name"] + "</option>";
	    }
	    $("#dataset_option1").empty();
	    $("#dataset_option1").append(SelectHtml);
	  }
	  getSubmitResult = function(page) {
	    page = parseInt(page);
	    let size = 5; //default
	    $.ajax({
	      url: RootUrl + '/api/judge/get?page=' + String(page) + '&size=' + String(size),
	      type: 'GET',
	      contentType: 'application/json',
	      async: true,
	      success: function(data) {
	        if (typeof(data) == "string") {
	          data = JSON.parse(data);
	        }
	        if (data["carpCases"].length > 0) {
	          $.cookie("page", page);
	          RenderSubmitResult(data["carpCases"], data["total"]);
	          let isRefresh = RefreshSubmitResult(data["carpCases"]);
	          if (isRefresh) {
	            setTimeout("getSubmitResult($.cookie('page'));", 3000);
	          }
	        } else if (parseInt(page) == 0) {
	          $.cookie("page", page);
	          RenderSubmitResult(data["carpCases"], 0);
	        }
	      }
	    });
	  }
	  var RenderSubmitResult = function(carpCases, total) {
	    let ResultHtml = "";
	    ResultHtml += "<thead>\n";
	    ResultHtml += "<tr>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		Id\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		Status\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		SubmitTime\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		Info\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		ExitCode\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		RunTime(s)\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		Result\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "	<th>\n";
	    ResultHtml += "		Dataset\n";
	    ResultHtml += "	<\/th>\n";
	    ResultHtml += "<\/tr>\n";
	    ResultHtml += "<\/thead>\n";
	    ResultHtml += "<tbody>\n";
	    let rId = carpCases.length;
	    if (rId == 0 || carpCases == undefined) {
	      ResultHtml += "<tr><td colspan='8' class='no_data_td'>No Data</td></tr>"
	    } else {
	      for (var o of carpCases) {
	        let dtime = new Date(o["submitTime"]);
	        let Submtime = (addzero(dtime.getMonth() + 1)) + '-' + addzero(dtime.getDate()) + ' ' + addzero(dtime.getHours()) + ':' + addzero(dtime.getMinutes()) + ':' + addzero(dtime.getSeconds());
	        let ErrorInfo = "";
	        let errorcolor = false;
	        let waitingcolor = false;
	        //未运行完成的程序是没有reason的
	        if (parseInt(o["status"]) < 3) {
	          waitingcolor = true;
	          ErrorInfo = "Waiting for result.";
	        } else {
	          if (o["valid"] == false || o["valid"] == "false") {
	            errorcolor = true;
	            ErrorInfo = o["reason"];
	          } else {
	            errorcolor = false;
	            waitingcolor = false;
	            ErrorInfo = o["reason"];
	          }
	        }
	        if (ErrorInfo == "") {
	          ErrorInfo = "No Error";
	          errorcolor = false;
	        }

	        let nowdataset = "";
	        for (var i in DataSetList) {
	          if (DataSetList[i]["id"] == o["datasetId"]) {
	            nowdataset = DataSetList[i]["name"];
	          }
	        }
	        if (nowdataset == "") {
	          nowdataset = "unknown";
	        }
	        ResultHtml += "<tr style='text-align:center;'>";
	        ResultHtml += "<td>\n";
	        ResultHtml += "	#" + rId + "\n";
	        ResultHtml += "<\/td>\n";
	        if (waitingcolor == true) {
	          ResultHtml += "<td class='waiting_color'>\n";
	        } else {
	          ResultHtml += "<td>\n";
	        }
	        ResultHtml += StatusCode[parseInt(o["status"], 10)];
	        ResultHtml += "<\/td>\n";
	        ResultHtml += "<td>\n";
	        ResultHtml += Submtime;
	        ResultHtml += "<\/td>\n";
	        if (errorcolor == true) {
	          ResultHtml += "<td class='error_color'>";
	        } else if (waitingcolor == true) {
	          ResultHtml += "<td class='waiting_color'>\n";
	        } else {
	          ResultHtml += "<td class='no_error_color'>\n";
	        }
	        ResultHtml += ErrorInfo;
	        ResultHtml += "<\/td>\n";
	        ResultHtml += "<td>\n";
	        ResultHtml += o["exitcode"];
	        ResultHtml += "<\/td>\n";
	        ResultHtml += "<td>\n";
	        ResultHtml += o["time"].toFixed(2);
	        ResultHtml += "<\/td>\n";
	        ResultHtml += "<td>\n";
	        ResultHtml += o["result"];
	        ResultHtml += "<\/td>\n";
	        ResultHtml += "<td>\n";
	        ResultHtml += nowdataset;
	        ResultHtml += "<\/td>\n";
	        ResultHtml += "<\/tr>\n";
	        rId--;
	      }
	    }
	    let page_left = isNaN(parseInt($.cookie("page"))) ? 0 : parseInt($.cookie("page")) + 1;
	    let page_right = Math.ceil(total / 5);
	    ResultHtml += "<\/tbody>\n";
	    ResultHtml += "<tfoot>\n";
	    ResultHtml += "<tr>\n";
	    ResultHtml += "	<td colspan=\"8\">\n";

	    ResultHtml += "Page : " + page_left + "/" + page_right + " ";
	    ResultHtml += "		<button class=\"btn btn-link\" id=\"page_left\"><i class=\"glyphicon glyphicon-chevron-left\"><\/i><\/button><button class=\"btn btn-link\" id=\"page_right\"><i class=\"glyphicon glyphicon-chevron-right\"><\/i><\/button>\n";
	    ResultHtml += "	<\/td>\n";
	    ResultHtml += "<\/tr>\n";
	    ResultHtml += "<\/tfoot>\n";
	    $("#result_table").empty();
	    $("#result_table").append(ResultHtml);

	    //Rebind the click events
	    (function() {
	      $("#page_left").click(function(event) {
	        if (!isLogin()) {
	          return;
	        }
	        let nowpage = parseInt($.cookie("page"));
	        if (isNaN(nowpage)) {
	          nowpage = 1;
	        }
	        if (nowpage > 0) {
	          getSubmitResult(nowpage - 1);
	        }
	      });
	      $("#page_right").click(function(event) {
	        if (!isLogin()) {
	          return;
	        }
	        let nowpage = parseInt($.cookie("page"));

	        if (isNaN(nowpage)) {
	          nowpage = -1;
	        }
	        getSubmitResult(nowpage + 1);
	      });
	    })();
	  }
	  var RefreshSubmitResult = function(carpCases) {
	    if (carpCases.length == 0) return false;
	    let isRefresh = false;
	    for (var i in carpCases) {
	      if (carpCases[i]["status"] < 3) {
	        isRefresh = true;
	      }
	    }
	    return isRefresh;
	  }
	  var getSubmitRemain = function() {
	    $.getJSON(RootUrl + "/api/judge/remain").success(function(data) {
	      if (typeof data == "string") {
	        data = JSON.parse(data);
	      }
	      let remainString = data["remain"] + '/' + data["total"] + ' remain';
	      RenderSubmitRemain(remainString);
	    }).fail(function(jqXHR, textStatus, errorThrown) {
	      if (parseInt(jqXHR["responseJSON"]["status"]) == 401) {
	        //session 过期
	        console.log("session out of date.");
	        $.cookie("username", null);
	        $.cookie("userid", null);
	        setTimeout(function() {
	          window.location.href = "index.html";
	        }, 500);
	      }
	      $("#info_box_msg").html("<p>It occurs a error when get remained amount, Error:" + jqXHR["responseJSON"]["message"] + "</p>")
	      $("#info_box").modal("show");
	      setTimeout(function() {
	        $("#info_box").modal("hide");
	      }, 5000);
	    });
	  }
	  var getSubmitQueue = function() {
	    $.getJSON(RootUrl + "/api/judge/queue").success(function(data) {
	      if (typeof data == "string") {
	        data = JSON.parse(data);
	      }
	      let queueLengthString = data["length"];
	      RenderSubmitQueue(queueLengthString);
	    }).fail(function(jqXHR, textStatus, errorThrown) {
	      if (parseInt(jqXHR["responseJSON"]["status"]) == 401) {
	        //session 过期
	        console.log("session out of date.");
	        $.cookie("username", null);
	        $.cookie("userid", null);
	        setTimeout(function() {
	          window.location.href = "index.html";
	        }, 500);
	      }
	      $("#info_box_msg").html("<p>It occurs a error when get remained amount, Error:" + jqXHR["responseJSON"]["message"] + "</p>")
	      $("#info_box").modal("show");
	      setTimeout(function() {
	        $("#info_box").modal("hide");
	      }, 5000);
	    });
	  }
	  var RenderSubmitRemain = function(str) {
	    $("#remain_number").html(str);
	  }
	  var RenderSubmitQueue = function(str) {
	    $("#queue_length").html(str + " in queue");
	  }
	  var getRankData = function() {
	    RenderTabList();
	    if (DataSetList.length) {
	      //clear RankData
	      RankData = {};
	      for (var d in DataSetList) {
	        (function(d) {
	          $.getJSON(RootUrl + "/api/judge/top?dataset=" + DataSetList[d]["id"]).success(function(data) {
	            if (typeof data == "string") {
	              data = JSON.parse(data);
	            }
	            let one_tab_rank = {};
	            one_tab_rank[DataSetList[d]["id"]] = data["carpCases"];
	            RankData[DataSetList[d]["id"]] = data["carpCases"];
	            if (d == 0) {
	              //默认渲染第一个DataSet的排名
	              RenderRankList(DataSetList[d]["id"]);
	            }
	          }).fail(function(jqXHR) {
	            if (parseInt(jqXHR["responseJSON"]["status"]) == 401) {
	              //session 过期
	              console.log("session out of date.");
	              $.cookie("username", null);
	              $.cookie("userid", null);
	              setTimeout(function() {
	                window.location.href = "index.html";
	              }, 500);
	            }
	            $("#info_box_msg").html("<p>It occurs a error when get Rank List data, Error:" + jqXHR["responseJSON"]["message"] + "</p>")
	            $("#info_box").modal("show");
	            setTimeout(function() {
	              $("#info_box").modal("hide");
	            }, 5000);
	          });
	        })(d)
	      }
	    }
	  }
	  RenderRankList = function(datasetid) {
	    //根据屏幕大小变化
	    if (parseInt($(window).width()) > 1400) {
	      $("#window_pannel").removeClass("col-md-8");
	      $("#window_pannel").addClass("col-md-10");
	    }
	    let render_data;
	    if (RankData[datasetid] == undefined) {
	      render_data = [];
	    } else {
	      render_data = RankData[datasetid].slice(0, 150);
	    }
	    let RankListHtml = "";
	    RankListHtml += "<thead>\n";
	    RankListHtml += "<tr>\n";
	    RankListHtml += "	<td>\n";
	    RankListHtml += "		Rank\n";
	    RankListHtml += "	<\/td>\n";
	    RankListHtml += "	<td>\n";
	    RankListHtml += "		User\n";
	    RankListHtml += "	<\/td>\n";
	    RankListHtml += "	<td>\n";
	    RankListHtml += "		submitTime\n";
	    RankListHtml += "	<\/td>\n";
	    RankListHtml += "	<td>\n";
	    RankListHtml += "		time\n";
	    RankListHtml += "	<\/td>\n";
	    RankListHtml += "	<td>\n";
	    RankListHtml += "		result\n";
	    RankListHtml += "	<\/td>\n";
	    RankListHtml += "<\/tr>\n";
	    RankListHtml += "<\/thead>\n";
	    RankListHtml += "<tbody id='page_box'>\n";
	    if (render_data == undefined || render_data.length == 0) {
	      RankListHtml += "<tr><td colspan='5' class='no_data_td'>No Data</td></tr>"
	    }
	    let ownrank = []
	    //pages分页
	    Page.items_num = render_data.length;
	    Page.page_num = Math.ceil(Page.items_num / Page.each_num);
	    Page.items = [];
	    Page.now_page = 0; //Where are you,default 0.
	    Page.page_html = "";
	    let user_rank_page_html = "";
	    let value;

	    for (var i = 0; i < Page.page_num; i++) {
	      for (var index = i * Page.each_num; index < (((i + 1) * Page.each_num) > Page.items_num ? Page.items_num : (i + 1) * Page.each_num); index++) {
	        value = render_data[index];
	        if ($.cookie("username") == value["userName"]) {
	          value["rank"] = parseInt(index) + 1;
	          if (ownrank)
	            ownrank.push(value);
	        }
	        let page_dom = "";
	        let dtime = new Date(value["submitTime"]);
	        let Submtime = (addzero(dtime.getMonth() + 1)) + '-' + addzero(dtime.getDate()) + ' ' + addzero(dtime.getHours()) + ':' + addzero(dtime.getMinutes()) + ':' + addzero(dtime.getSeconds());
	        let rank = parseInt(index) + 1;
	        page_dom += "<tr>\n";
	        page_dom += "	<td>\n";
	        page_dom += "#" + rank;
	        page_dom += "	<\/td>\n";
	        page_dom += "	<td>\n";
	        page_dom += value["userName"];
	        page_dom += "	<\/td>\n";
	        page_dom += "	<td>\n";
	        page_dom += Submtime;
	        page_dom += "	<\/td>\n";
	        page_dom += "	<td>\n";
	        page_dom += value["time"].toFixed(3);
	        page_dom += "	<\/td>\n";
	        page_dom += "	<td>\n";
	        page_dom += value["result"];
	        page_dom += "	<\/td>\n";
	        page_dom += "<\/tr>\n";
	        Page.items.push(page_dom);
	      }
	    }
	    // 生成页面按钮
	    let page_active = "";
	    for (var i = 0; i < Page.page_num; i++) {
	      if (i == Page.now_page) {
	        page_active = "page_active";
	      } else {
	        page_active = "";
	      }
	      Page.page_html += "<button class='btn btn-default btn-sm " + page_active + "' onclick='change_page(" + i + ")'>" + (parseInt(i) + 1) + "</button>";
	    }

	    //Default : show page which in you are.log((((i+1)*Page.each_num)>Page.items_num?Page.items_num:(i+1)*Page.each_num))

	    for (var index = Page.now_page * Page.each_num; index < (((Page.now_page + 1) * Page.each_num) > Page.items_num ? Page.items_num : (Page.now_page + 1) * Page.each_num); index++) {
	      user_rank_page_html += Page.items[index];
	    }
	    user_rank_page_html += "<td colspan='5'>" +
	      // "<button class='btn btn-info btn-sm' id='page-left'><i class='glyphicon glyphicon-arrow-left'></i></button>" +
	      Page.page_html +
	      // "<button class='btn btn-info btn-sm' id='page-right'><i class='glyphicon glyphicon-arrow-right'></i></button>" +
	      "</td>";
	    RankListHtml += user_rank_page_html;
	    RankListHtml += "<\/tbody>\n";
	    let MyRankHtml = "";
	    // MyRankHtml += "<tr class='warning'><td colspan='1'>Your Rank</td><td colspan='4'></td></tr>";
	    if (ownrank.length == 0) {
	      MyRankHtml += "<tr>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += "# -";
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += "--------- ";
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += "-";
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += "-";
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "<\/tr>\n";
	    }
	    //只选择最小的前10个
	    _.sortBy(ownrank, function(each) {
	      return each["rank"];
	    });

	    for (var i in ownrank.slice(0, 10)) {
	      let dtime = new Date(ownrank[i]["submitTime"]);
	      let Submtime = (dtime.getMonth() + 1) + '-' + dtime.getDate() + ' ' + dtime.getHours() + ':' + dtime.getMinutes() + ':' + dtime.getSeconds();
	      let rank = ownrank[i]["rank"]
	      MyRankHtml += "<tr>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += "#" + rank;
	      MyRankHtml += "	<\/td>\n";

	      MyRankHtml += "	<td>\n";
	      MyRankHtml += Submtime;
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += ownrank[i]["time"].toFixed(3);
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "	<td>\n";
	      MyRankHtml += ownrank[i]["result"];
	      MyRankHtml += "	<\/td>\n";
	      MyRankHtml += "<\/tr>\n";
	    }
	    // RankListHtml += "<tfoot>" + MyRankHtml + "<\/tfoot>";
	    $("#rank_table").empty();
	    $("#rank_table").append(RankListHtml);
	    $("#myRank_table").find("tbody").empty();
	    $("#myRank_table").find("tbody").append(MyRankHtml);

	  }
	  var RenderTabList = function() {
	    $("#rank_tab_list").empty();
	    let RankTabHtml = "";
	    if (DataSetList.length == 0) {
	      RankTabHtml += '<li role="presentation"><a href="#">No DataSet.</a></li>';
	    } else {
	      for (var d in DataSetList) {
	        if (d == 0) {
	          RankTabHtml += '<li role="presentation" class="active"><a href="#" id="' + DataSetList[d]["id"] + '" onclick="changeRankTab(this);">' + DataSetList[d]["name"] + '</a></li>';
	        } else {
	          RankTabHtml += '<li role="presentation"><a href="#" id="' + DataSetList[d]["id"] + '" onclick="changeRankTab(this);">' + DataSetList[d]["name"] + '</a></li>';
	        }
	      }
	    }
	    $("#rank_tab_list").append(RankTabHtml);
	    return true;
	  }

	  $(function() {
	    mobileMenuOutsideClick();
	    offcanvasMenu();
	    burgerMenu();
	    contentWayPoint();
	    sliderMain();
	    dropdown();
	    goToTop();
	    loaderPage();
	    counterWayPoint();
	    fullHeight();
	    checkCookie();
	    getDatasetList();
	    getRankData();
	    $("#result_submit").click(function(event) {
	      var submit_data = {
	        "id": "",
	        "data": ""
	      };
	      submit_data["id"] = $("#graph_choose option:selected").attr("value");
	      submit_data["data"] = $("#carp_result").val();
	      submit_data["userId"] = $.cookie("userid");
	      $.ajax({
	        url: RootUrl + '/api/judge/submit',
	        type: 'POST',
	        headers: {
	          "X-XSRF-TOKEN": $.cookie("XSRF-TOKEN")
	        },
	        xhrFields: {
	          withCredentials: true
	        },
	        contentType: 'application/json',
	        async: true,
	        data: JSON.stringify(submit_data),
	        success: function(data) {
	          if (typeof(data) == "string") {
	            data = JSON.parse(data);
	          }
	          if (data["status"] == 200) {
	            $("#info_box_msg").html("<p>Submit Successfully! <br /> " + data["data"]["result"] + "</p>")
	            $("#info_box").modal("show");
	            setTimeout(function() {
	              $("#info_box").modal("hide");
	            }, 5000);
	          }
	        }
	      });
	    });
	    $(".login").each((i, e) => {

	      $(e).click(function(event) {
	        /* Act on the event */
	        $("#login_box").modal("show");
	      });
	    });

	    $("#login_submit").click(function(event) {
	      /* Act on the event */
	      let user = $("#username").val();
	      let pwd = $("#password").val();
	      let submit_data = {};
	      submit_data["username"] = user;
	      submit_data["password"] = pwd;
	      $.ajax({
	        url: RootUrl + '/api/login',
	        type: 'POST',
	        contentType: 'application/json',
	        async: false,
	        headers: {
	          "X-XSRF-TOKEN": $.cookie("XSRF-TOKEN")
	        },
	        xhrFields: {
	          withCredentials: true
	        },
	        data: JSON.stringify(submit_data),
	        success: function(data) {
	          console.log('login successly!');
	          if (typeof(data) == "string") {
	            data = JSON.parse(data);
	          }
	          var expDate = new Date();
	          expDate.setTime(expDate.getTime() + (7 * 24 * 3600 * 1000));
	          $.cookie("userid", data["uid"], {
	            expires: expDate
	          });
	          $.cookie("username", user, {
	            expires: expDate
	          });

	          $("#login_box").modal("hide");
	          $(".user_place").each(function(i, e) {
	            $(e).css("display", "none");
	          });
	          $(".user_span").each(function(i, e) {
	            $(e).empty();
	            $(e).append("<a href='#'  class='pop_logout' onclick='logout_user()'><span id='logout'>&nbsp;&nbsp;Hello," + user + "&nbsp;&nbsp;</span></a>");
	            $(e).css("display", "inline-block");
	            $(".pop_logout").mouseover(function(event) {
	              let remain = $(this).html();
	              $(this).html("<span class='logout_span'>&nbsp;&nbsp; Logout &nbsp;&nbsp;</span>");
	              let self = this;
	            });
	            $(".pop_logout").mouseleave(function(event) {
	              $(this).html("<span id='logout'>&nbsp;&nbsp;Hello," + user + "&nbsp;&nbsp;</span>");
	            });
	            $(".logout_span").click(function() {
	              console.log("...");
	              logout_user();
	            });
	          });
	          $(".change_pwd").each(function(i, e) {
	            $(e).css("display", "inline-block");
	          });
	          //登录之后拉取提交记录
	          getSubmitResult(0);
	          //登录之后拉取提交剩余次数
	          getSubmitRemain();
	          getSubmitQueue();
	        },
	        error: function(jqXHR, textStatus, errorThrown) {
	          if (parseInt(jqXHR["responseJSON"]["status"]) == 401) {
	            //session 过期
	            $.cookie("username", null);
	            $.cookie("userid", null);
	          } else if (parseInt(jqXHR["responseJSON"]["status"]) == 400 && jqXHR["responseJSON"]["message"] == "Already logged in!") {
	            $.getJSON(RootUrl + "/api/user/info").success(function(data) {
	              if (typeof data == "string") {
	                data = JSON.parse(data);
	              }
	              var expDate = new Date();
	              expDate.setTime(expDate.getTime() + (7 * 24 * 3600 * 1000));
	              $.cookie("userid", data["uid"], {
	                expires: expDate
	              });
	              $.cookie("username", user, {
	                expires: expDate
	              });
	              $("#login_box").modal("hide");
	              $(".user_place").each(function(i, e) {
	                $(e).css("display", "none");
	              });
	              $(".user_span").each(function(i, e) {
	                $(e).empty();
	                $(e).append("<a href='#' class='pop_logout' onclick='logout_user()'><span id='logout'>&nbsp;&nbsp;Hello," + user + "&nbsp;&nbsp;</span></a>");
	                $(e).css("display", "inline-block");
	                $(".pop_logout").mouseover(function(event) {
	                  let remain = $(this).html();
	                  $(this).html("<span class='logout_span'>&nbsp;&nbsp; Logout &nbsp;&nbsp;</span>");
	                  let self = this;
	                });
	                $(".pop_logout").mouseleave(function(event) {
	                  $(this).html("<span id='logout'>&nbsp;&nbsp;Hello," + user + "&nbsp;&nbsp;</span>");
	                });
	                $("#logout_span").click(function() {
	                  console.log("...");
	                  logout_user()
	                });
	              });
	              $(".change_pwd").each(function(i, e) {
	                $(e).css("display", "inline-block");
	              });
	              //登录之后拉取提交记录
	              getSubmitResult(0);
	              //登录之后拉取提交剩余次数
	              getSubmitRemain();
	              getSubmitQueue();
	            });
	            return;
	          }
	          $("#login_box").modal("hide");
	          $("#info_box_msg").html("<p>It occurs a error when submit data, Error:" + jqXHR["responseJSON"]["message"] + "</p>")
	          $("#info_box").modal("show");
	          setTimeout(function() {
	            $("#info_box").modal("hide");
	          }, 5000);
	        }
	      });
	    });
	    $("#forget_password").click(function(event) {
	      /* Act on the event */
	      $("#login_box").modal("hide");
	      // $("#registry_box").modal("show");
	    });

	    //提交zip文件並上傳，附帶所使用的dataset，用於判別程序運行結果
		$("#code_submit2").click(function(event) {
			$("#warning_box").modal("show");
		});
	    $("#btn-warn-yes").click(function(event) {
	      /* Act on the event */
	      //submit file as base64.
	      let zipcode = document.getElementById("code_submit").files[0];
	      var reader = new FileReader();
	      reader.readAsDataURL(zipcode);
	      reader.onload = function(e) {
	        let submit_data = {
	          "dataset": "",
	          "data": ""
	        };
	        submit_data["dataset"] = $("#graph_choose1 option:selected").attr("value");
	        let rowdata = e.target.result;
	        let base64_pos = rowdata.indexOf("base64,");
	        let base64data = rowdata.substr(base64_pos + 7, rowdata.length);

	        submit_data["data"] = base64data;
	        $.ajax({
	          url: RootUrl + '/api/judge/submit',
	          type: 'POST',
	          headers: {
	            "X-XSRF-TOKEN": $.cookie("XSRF-TOKEN")
	          },
	          xhrFields: {
	            withCredentials: true
	          },
	          contentType: 'application/json',
	          data: JSON.stringify(submit_data),
	          success: function(data) {
	            if (typeof(data) == "string") {
	              data = JSON.parse(data);
	            }
	            $("#info_box_msg").html("<p>Submit Successfully!</p>")
	            $("#info_box").modal("show");
	            setTimeout(function() {
	              $("#info_box").modal("hide");
	            }, 5000);
	            //登录之后拉取提交记录
	            getSubmitResult(0);
	            //登录之后拉取提交剩余次数
	            getSubmitRemain();
	            getSubmitQueue();
	          },
	          error: function(jqXHR, textStatus, errorThrown) {
	            if (parseInt(jqXHR["responseJSON"]["status"]) == 401) {
	              //session 过期
	              console.log("session out of date.");
	              $.cookie("username", null);
	              $.cookie("userid", null);
	              setTimeout(function() {
	                window.location.href = "index.html";
	              }, 5000);
	            }
	            $("#info_box_msg").html("<p>It occurs a error when submit data, Error:" + jqXHR["responseJSON"]["message"] + "</p>")
	            $("#info_box").modal("show");
	            setTimeout(function() {
	              $("#info_box").modal("hide");
	            }, 5000);
	          }
	        });

	      }

	    });
	    $("#page_left").click(function(event) {
	      if (!isLogin()) {
	        return;
	      }
	      let nowpage = parseInt($.cookie("page"));
	      if (isNaN(nowpage)) {
	        nowpage = 1;
	      }
	      if (nowpage > 0) {
	        getSubmitResult(nowpage - 1);
	      }
	    });
	    $("#page_right").click(function(event) {
	      if (!isLogin()) {
	        return;
	      }
	      let nowpage = parseInt($.cookie("page"));
	      if (isNaN(nowpage)) {
	        nowpage = -1;
	      }
	      getSubmitResult(nowpage + 1);
	    });
	    $("#change_pwd_submit").click(function(event) {
	      /* Act on the event */
	      $("#change_pwd_submit").attr("disabled", "disabled");
	      let pwd = $("#change_password").val();
	      let newpwd = $("#change_new_password").val();
	      let send_data = {
	        "old": pwd,
	        "new": newpwd
	      };
	      $.ajax({
	        url: RootUrl + '/api/user/change/password',
	        type: 'POST',
	        headers: {
	          "X-XSRF-TOKEN": $.cookie("XSRF-TOKEN")
	        },
	        xhrFields: {
	          withCredentials: true
	        },
	        contentType: 'application/json',
	        data: JSON.stringify(send_data),
	        success: function(data) {
	          if (typeof(data) == "string") {
	            data = JSON.parse(data);
	          }
	          $("#change_pwd_content").html("<p>Change Successfully!<br/>Refresh page after 3 seconds.</p>")
	          $.cookie("username", null);
	          $.cookie("userid", null);
	          setTimeout(function() {
	            $("#change_pwd_box").modal("hide");
	            window.location.href = "index.html";
	          }, 3000);
	        },
	        error: function(jqXHR, textStatus, errorThrown) {
	          if (parseInt(jqXHR["responseJSON"]["status"]) == 401) {
	            //session 过期
	            console.log("session out of date.");
	            $.cookie("username", null);
	            $.cookie("userid", null);
	            logout_user();
	            setTimeout(function() {
	              window.location.href = "index.html";
	            }, 5000);
	          }
	          $("#change_pwd_content").html("<p>It occurs a error when submit data, Error:" + jqXHR["responseJSON"]["message"] + "</p>");
	          setTimeout(function() {
	            let change_pwdHtml = "<div class=\"form-group\">\n";
	            change_pwdHtml += "	<label for=\"password\">Password<\/label>\n";
	            change_pwdHtml += "	<input type=\"password\" class=\"form-control\" name=\"password\" id=\"change_password\" placeholder=\"Password\">\n";
	            change_pwdHtml += "<\/div>\n";
	            change_pwdHtml += "<div class=\"form-group\">\n";
	            change_pwdHtml += "	<label for=\"newpassword\">New Password<\/label>\n";
	            change_pwdHtml += "	<input type=\"password\" class=\"form-control\" name=\"newpassword\" id=\"change_new_password\" placeholder=\"New Password\">\n";
	            change_pwdHtml += "<\/div>\n";
	            $("#change_pwd_box").modal("hide");
	            $("#change_pwd_content").html(change_pwdHtml);
	            $("#change_pwd_submit").removeAttr("disabled");
	          }, 3000);
	        }
	      });
	    });
	  });
	})()