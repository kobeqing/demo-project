import groovy.json.JsonSlurper;
/*
//this is an example about how to run a basic shell command in groovy
def command= "curl -XGET http://eselnlx1951.mo.sw.ericsson.se:9200/spitfire-master-gerrit/_search?size=2&pretty";
def proc = command.execute();
proc.waitFor();
def text = proc.in.text;
println "stdout : ${text}";

//this is an example about how to use jsonBuild to create a json in groovy way
def builder = new groovy.json.JsonBuilder()
        def root = builder.people {
            person {
                firstName 'Guillame'
                lastName 'Laforge'
                if (1 == 0) {
                    address {
                        city 'Paris'
                        country 'France'
                        zip 12345
                    }
                }
                married true
                // a list of values
                conferences 'JavaOne', 'Gr8conf'
            }
        }
*/

def sluper = new JsonSlurper()
def data = sluper.parse(new FileReader('/home/ejiaqsu/comments_log_json'))


def commentMap = [test40: false, build: false, ut: false, coverity: false, test50: false];

//use jsonBuilder to create json
def builder = new groovy.json.JsonBuilder()
def root = builder {
    id data.project + "_" + data.id
    change_id data.id
    commit "6350fbbc749b0a333cf95d7f1393a419d8d09712"
    seen_date "2016-07-06T05:42:09+0200"
    version_info ""
    review_project data.project
    review_branch data.branch
    number data.number
    subject data.commitMessage
    status data.status
    user data.owner.name
    username data.owner.username
    email data.owner.email
    create_date "2016-07-04T08:21:54+0200"

    for(def i = data.comments.size() -1; i >= 0; i--) {
        def lines = data.comments.get(i).message.split('\n');
        lines.each {
            if (it.contains('ngrlft_40_verification') && !commentMap.test40) {
                commentMap.test40 = true;
                '40test_info' it;
            } else if (it.contains('ngrlft_build_unitest') && !commentMap.ut) {
                commentMap.ut = true;
                'ut_info' it;
            } else if (it.contains('ngrlft_SA_codecheck') && !commentMap.coverity) {
                commentMap.coverity = true;
                'coverity_info' it;
            } else if (it.contains('ngrlft_build_patchset') && !commentMap.build) {
                commentMap.build = true;
                'build_info' it;
            } else if (it.contains('ngrlft_build_unitest') && !commentMap.test50) {
                commentMap.test50 = true;
                '50build_info' it;
            }
        }
    }
}

def dataJson = builder.toString();

//use processbuilder to run a complex shell command
def proc = new ProcessBuilder(['curl', '-XPUT', 'http://147.128.112.106:9100/ejiaqsu_commit/commit/' + data.id, '-d', dataJson]).start();
proc.waitFor();
def text = proc.in.text;
println "stdout : ${text}";
