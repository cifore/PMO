var currentPage = "";//当前页码
var pageCount = "";//共几页 
$(function() {
	loadCandidateList();
	
//	var status = document.getElementById('feedBackDialog').getAttribute('aria-hidden');
//	if(status == 'false'){
//		validate();
//	}
	
});

$('#interviewForm').bootstrapValidator({
	feedbackIcons: {
        valid: 'glyphicon glyphicon-ok',
        invalid: 'glyphicon glyphicon-remove',
        validating: 'glyphicon glyphicon-refresh'
	},
	fields : {
		interviewStatus : {
			group : '.group',
			validators : {
				notEmpty : {
					message : '请选择'
				},
			}
		},
		interviewFeedBack : {
			group : '.group',
			validators : {
				notEmpty : {
					message : '请输入反馈信息'
				},
			}
		}
	}
}).on('success.form.bv', function(e) {
    // Prevent submit form
    e.preventDefault();

    var $form = $(e.target);
        validator = $form.data('bootstrapValidator');
    if(validator){
    	updateInterviewFeedBack(e.target);
    }
    return false;
}) ;

function loadCandidateList(pageState) {
	var candidate = new FormData(document.getElementById("candidateForm"));
	if (null != pageState) {
		candidate.append("pageState", pageState);
	}
	candidate.append("currentPage", currentPage);
	candidate.append("pageCount", pageCount);
	$.ajax({
				url : path + "/service/candidate/queryInterviewFeedBack",
				dataType : "json",
				async : true,
				data : candidate,
				cache : false,
				processData : false,
				contentType : false,
				type : "post",
				success : function(result) {
					if(result==null){
						return;
					}
					$("#candidateList tbody").remove();
					var tbody = $("<tbody>");
					tbody.appendTo($("#candidateList"));
					for (var i = 0; i < result.data.length; i++) {
						var tr = $("<tr></tr>");
						tr.appendTo(tbody);
						$("<td>" + result.data[i].interviewDate + "</td>")
								.appendTo(tr);
						$("<td>"+ result.data[i].candidateName + "</td>")
								.appendTo(tr);
						$("<td>" + result.data[i].candidateSex + "</td>")
								.appendTo(tr);
						$("<td>" + result.data[i].candidateAge + "</td>")
								.appendTo(tr);
						$("<td>" + result.data[i].candidateTel + "</td>")
								.appendTo(tr);
						$("<td>" + result.data[i].email + "</td>").appendTo(tr);
						$("<td>" + result.data[i].role + "</td>").appendTo(tr);
						$("<td>" + result.data[i].experienceYears + "</td>")
								.appendTo(tr);
						$("<td>" + result.data[i].englishLevel + "</td>")
								.appendTo(tr);
						$("<td>" + result.data[i].skill + "</td>").appendTo(tr);
						$("<td><a href='javascript:void(0);' class='btn btn-info btn-small' "
								+ "onclick=interviewFeedBack('"
								+ result.data[i].interviewId + "','"
								+ result.data[i].candidateName + "','"
								+ result.data[i].candidateId + "','"
								+ result.data[i].nickName + "','"
								+ result.data[i].csSubdeptName
								+ "')>FeedBack</a>&nbsp;&nbsp;<a href='javascript:void(0);' class='btn btn-info btn-small'"
								+ "onclick=downLoadCandidateResume('"
								+ result.data[i].interviewId +"','"
								+result.data[i].resumePath.replace(/\s+/g, "")
								+"')>RESUME</a></td>").appendTo(tr);
					}
					$("#candidateList").append("</tbdoy>");
					currentPage = parseInt(result.pageInfo.currentPage);
					pageCount = parseInt(result.pageInfo.pageCount);
					var pageDataCount = parseInt(result.pageInfo.pageDataCount);
					var dataCount = parseInt(result.pageInfo.dataCount);
					$("#pageCount").html(pageCount);
					$("#currentPage").html(currentPage);
					$("#pageDataCount").html(pageDataCount);
					$("#dataCount").html(dataCount);
					$("#nextPage").attr("onclick", "loadCandidateList('next')");
					$("#previousPage").attr("onclick",
							"loadCandidateList('previous')");
					$("#lastPage").attr("onclick", "loadCandidateList('last')");
					$("#fristPage").attr("onclick",
							"loadCandidateList('frist')");
					if (currentPage == pageCount) {
						$("#nextPage").removeAttr("onclick");
						$("#lastPage").removeAttr("onclick");
					}
					if (currentPage == 1) {
						$("#previousPage").removeAttr("onclick");
						$("#fristPage").removeAttr("onclick");
					}
				}
			})
}

function interviewFeedBack(interviewId, candidateName, candidateId, interviewName, csSubDeptName) {
	$('#interviewFeedBack').val("");
	$('#interviewStatus').val("");
	$(".has-feedback").removeClass("has-feedback");
	$(".has-success").removeClass("has-success");
	$(".has-error").removeClass("has-error");
	$("small").css("display","none");
	$(".form-control-feedback").css("display","none");
	$("#interviewId").val(interviewId);
	$('#interviewName').val(interviewName);
	$('#candidateName').val(candidateName);
	$('#candidateId').val(candidateId);
	$('#csSubDeptName').val(csSubDeptName);
	$('#feedBackDialog').modal('show');
}

function downLoadCandidateResume(candidateId,resumePath){
	if(resumePath == null || resumePath == ''){
		alert("未上传此候选人简历");
		return;
	}
	var url = path+'/service/candidate/downLoadCandidateResume?candidateId='+candidateId;
	$("#exceltHrefCandidate").attr("href",url);
	document.getElementById("exceltHrefCandidate").click();
}

function updateInterviewFeedBack(e) {
	var interviewId = $('#interviewId').val();
	var feedBackInfo = $('#interviewFeedBack').val();
	var interviewStatus = $('#interviewStatus').val();
	var candidateId = $('#candidateId').val();
	
	// 取选中的text值
	// $('#interviewStatus').find("option:selected").text()
	$.ajax({
		url : path + '/service/candidate/updateInterviewFeedBack',
		dataType : "json",
		data : {
			"interviewId" : interviewId,
			"feedBackInfo" : feedBackInfo,
			"interviewStatus" : interviewStatus,
			"candidateId" : candidateId
		},
		async : true,
		cache : false,
		type : "post",
		success : function(resultFlag) {
			if (resultFlag) {
				$('#feedBackDialog').modal('hide');
				loadCandidateList();
			}
		}
	})
}


//if($("#GRADUATE_DATE1").length != 0){
//	  $('#candidateForm').data("bootstrapValidator").revalidateField($("#GRADUATE_DATE1"));
//}
